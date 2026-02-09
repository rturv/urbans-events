package com.urbanevents.metricas.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.metricas.domain.IncidenciaMetrica;
import com.urbanevents.metricas.service.AgregacionMetricasService;
import com.urbanevents.metricas.service.CalculoMetricasService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Consumidor de eventos: incidencias.priorizadas
 * Escucha los eventos cuando se prioriza una incidencia.
 */
@ApplicationScoped
public class IncidenciaPriorizadaConsumer {

    private static final Logger LOG = Logger.getLogger(IncidenciaPriorizadaConsumer.class);

    @Inject
    AgregacionMetricasService agregacionMetricasService;

    @Inject
    CalculoMetricasService calculoMetricasService;

    @Inject
    com.urbanevents.metricas.domain.IncidenciaMetricaRepository incidenciaMetricaRepository;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Recibe mensajes del topic "incidencias-priorizadas-metricas"
     * y los procesa deserializando a IncidenciaPriorizadaEvent.
     */
    @Incoming("incidencias-priorizadas-metricas")
    public Uni<Void> consume(String mensaje) {
        return Uni.createFrom().item(mensaje)
            .onItem().transform(json -> {
                try {
                    LOG.debugf("Recibido mensaje en incidencias-priorizadas-metricas: %s", json);
                    return objectMapper.readValue(json, IncidenciaPriorizadaEvent.class);
                } catch (Exception e) {
                    throw new RuntimeException("Error deserializando evento", e);
                }
            })
            .onItem().transformToUni(evento ->
                Panache.withTransaction(() ->
                    incidenciaMetricaRepository.find("incidenciaId", evento.incidenciaId())
                        .firstResult()
                        .onItem().transformToUni(metrica -> {
                                if (metrica == null) {
                                IncidenciaMetrica nueva = new IncidenciaMetrica(
                                        evento.incidenciaId(),
                                        "DESCONOCIDO",
                                        evento.priorizadaEn()
                                );
                                nueva.prioridad = evento.prioridad();
                                nueva.tiempoPriorizacion = evento.priorizadaEn();
                                nueva.ultimaActualizacion = java.time.Instant.now();
                                return incidenciaMetricaRepository.persist(nueva);
                            } else {
                                metrica.prioridad = evento.prioridad();
                                metrica.tiempoPriorizacion = evento.priorizadaEn();

                                if (metrica.tiempoCreacion != null) {
                                    long msPriorizacion = calculoMetricasService.calcularTiempoEnMs(
                                            metrica.tiempoCreacion,
                                            evento.priorizadaEn()
                                    );
                                    metrica.msPriorizacion = msPriorizacion;
                                }

                                metrica.ultimaActualizacion = java.time.Instant.now();
                                return incidenciaMetricaRepository.persist(metrica);
                            }
                        })
                )
                .onItem().transformToUni(v ->
                    Uni.createFrom().voidItem()
                        .runSubscriptionOn(io.smallrye.mutiny.infrastructure.Infrastructure.getDefaultExecutor())
                        .onItem().invoke(() -> agregacionMetricasService.recalcularPorTipoYPrioridad(
                                "DESCONOCIDO", evento.prioridad()
                        ))
                )
            )
            .onFailure().invoke(e -> LOG.errorf(e, "Error procesando mensaje IncidenciaPriorizadaEvent: %s. Mensaje: %s", e.getMessage(), mensaje))
            .onFailure().recoverWithNull();
    }
}
