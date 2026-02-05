package com.urbanevents.notificaciones.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaNotificadaEvent;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.notificaciones.domain.Notificacion;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class IncidenciaPriorizadaConsumer {

    private static final Logger LOG = Logger.getLogger(IncidenciaPriorizadaConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "notificaciones.destinatarios")
    String destinatarios;

    @Incoming("incidencias-priorizadas")
    @Outgoing("incidencias-notificadas")
    public Uni<Record<String, String>> onMessage(String eventJson) {
        return Uni.createFrom().item(eventJson)
            .onItem().transform(json -> {
                try {
                    return objectMapper.readValue(json, IncidenciaPriorizadaEvent.class);
                } catch (Exception e) {
                    throw new RuntimeException("Error deserializando evento", e);
                }
            })
            .onItem().transformToUni(event -> {
                LOG.infof("Recibida incidencia priorizada: %s con prioridad %s", event.incidenciaId(), event.prioridad());

                // Filtrar solo incidencias de alta prioridad
                if (!"alta".equalsIgnoreCase(event.prioridad())) {
                    LOG.infof("Incidencia %s con prioridad %s no será notificada", event.incidenciaId(), event.prioridad());
                    return Uni.createFrom().nullItem();
                }

                Instant now = Instant.now();
                String canal = "email";
                String estado = "enviada";

                // Guardar notificación en la base de datos de forma reactiva con transacción
                Notificacion notificacion = new Notificacion(event.incidenciaId(), canal, destinatarios, estado, now);
                
                return Panache.withTransaction(() -> notificacion.persist())
                    .onItem().transform(persisted -> {
                        LOG.infof("Notificación guardada para incidencia %s", event.incidenciaId());
                        
                        // Crear metadata del evento
                        EventMetadata metadata = new EventMetadata(
                            UUID.randomUUID().toString(),
                            "IncidenciaNotificada",
                            now,
                            "notificaciones-quarkus",
                            "v1"
                        );

                        // Crear evento de notificación
                        IncidenciaNotificadaEvent notificadaEvent = new IncidenciaNotificadaEvent(
                            metadata,
                            event.incidenciaId(),
                            canal,
                            destinatarios,
                            estado,
                            now
                        );

                        LOG.infof("Enviando evento de notificación para incidencia %s", event.incidenciaId());

                        try {
                            // Serializar el evento a JSON
                            String eventJsonOut = objectMapper.writeValueAsString(notificadaEvent);
                            // Retornar el record para publicar en Kafka
                            return Record.of(String.valueOf(event.incidenciaId()), eventJsonOut);
                        } catch (Exception e) {
                            LOG.errorf(e, "Error serializando evento");
                            throw new RuntimeException(e);
                        }
                    });
            })
            .onFailure().invoke(e -> LOG.errorf(e, "Error procesando mensaje"))
            .onFailure().recoverWithNull();
    }
}
