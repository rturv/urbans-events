package com.urbanevents.metricas.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.metricas.domain.IncidenciaMetrica;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Consumidor de eventos: incidencias.creadas
 * Escucha los eventos cuando se crea una nueva incidencia.
 */
@ApplicationScoped
public class IncidenciaCreadaConsumer {

    private static final Logger LOG = Logger.getLogger(IncidenciaCreadaConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    com.urbanevents.metricas.domain.IncidenciaMetricaRepository incidenciaMetricaRepository;

    /**
     * Recibe mensajes del topic "incidencias-creadas-metricas"
     * y los procesa deserializando a IncidenciaCreadaEvent.
     */
    @Incoming("incidencias-creadas-metricas")
    public Uni<Void> consume(String mensaje) {
        return Uni.createFrom().item(mensaje)
            .onItem().transform(json -> {
                try {
                    LOG.debugf("Recibido mensaje en incidencias-creadas-metricas: %s", json);
                    return objectMapper.readValue(json, IncidenciaCreadaEvent.class);
                } catch (Exception e) {
                    throw new RuntimeException("Error deserializando evento", e);
                }
            })
            .onItem().transformToUni(evento -> {
                LOG.infof("Procesando IncidenciaCreadaEvent para incidencia %d, tipo: %s",
                        evento.incidenciaId(), evento.tipo());

                IncidenciaMetrica metrica = new IncidenciaMetrica(
                        evento.incidenciaId(),
                        evento.tipo(),
                        evento.creadaEn()
                );

                metrica.estadoActual = com.urbanevents.metricas.domain.EstadoIncidencia.PENDIENTE;
                metrica.ultimaActualizacion = java.time.Instant.now();

                return Panache.withTransaction(() -> incidenciaMetricaRepository.persist(metrica))
                        .onItem().invoke(v -> LOG.infof("Incidencia %d creada (reactivo)", evento.incidenciaId()))
                        .replaceWithVoid();
            })
            .onFailure().invoke(e -> LOG.errorf(e, "Error procesando mensaje IncidenciaCreadaEvent: %s. Mensaje: %s", e.getMessage(), mensaje))
            .onFailure().recoverWithNull();
    }
}
