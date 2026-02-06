package com.urbanevents.notificaciones.observer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaNotificadaEvent;
import com.urbanevents.notificaciones.domain.Notificacion;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.UUID;

/**
 * Clase base abstracta para observers de notificaciones.
 * Proporciona la funcionalidad común de publicar eventos en Kafka.
 */
public abstract class NotificacionObserverBase {

    private static final Logger LOG = Logger.getLogger(NotificacionObserverBase.class);

    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    @Channel("incidencias-notificadas")
    protected MutinyEmitter<Record<String, String>> emitter;

    /**
     * Publica el evento de notificación en la cola de Kafka.
     * Método compartido por todos los observers de notificaciones.
     */
    protected Uni<Void> publicarEventoNotificada(Notificacion notificacion) {
        try {
            Instant now = Instant.now();
            
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
                notificacion.incidenciaId,
                notificacion.canal,
                notificacion.destinatario,
                notificacion.estado.name(),
                now
            );

            // Serializar el evento a JSON
            String eventJson = objectMapper.writeValueAsString(notificadaEvent);
            
            // Publicar en Kafka de forma reactiva
            Record<String, String> record = Record.of(String.valueOf(notificacion.incidenciaId), eventJson);
            
            return emitter.send(record)
                .invoke(() -> LOG.infof("Evento publicado en Kafka para incidencia %d", notificacion.incidenciaId))
                .onFailure().invoke(e -> LOG.errorf(e, "Error al publicar evento en Kafka para notificación %d", notificacion.id));
        } catch (Exception e) {
            LOG.errorf(e, "Error al serializar evento para notificación %d", notificacion.id);
            return Uni.createFrom().failure(e);
        }
    }
}
