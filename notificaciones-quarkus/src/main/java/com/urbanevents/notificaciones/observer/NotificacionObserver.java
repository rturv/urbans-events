package com.urbanevents.notificaciones.observer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaNotificadaEvent;
import com.urbanevents.notificaciones.domain.EstadoNotificacion;
import com.urbanevents.notificaciones.domain.Notificacion;
import com.urbanevents.notificaciones.domain.NotificacionCreadaEvent;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

/**
 * Observador que escucha eventos de creación de notificaciones y envía emails.
 * Implementa el patrón Observer para desacoplar la lógica de persistencia de la lógica de envío de emails.
 */
@ApplicationScoped
public class NotificacionObserver {

    private static final Logger LOG = Logger.getLogger(NotificacionObserver.class);

    @Inject
    ReactiveMailer mailer;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Channel("incidencias-notificadas")
    MutinyEmitter<Record<String, String>> emitter;

    /**
     * Observa el evento de creación de notificación.
     * Si el email se envía correctamente, actualiza la notificación con estado ENVIADA y publica en Kafka.
     * Si falla, actualiza el estado a ERROR.
     *
     * @param event El evento que contiene la notificación creada
     */
    public void onNotificacionCreada(@Observes NotificacionCreadaEvent event) {
        Notificacion notificacion = event.getNotificacion();
        
        // Crear contenido del email basado en la notificación
        String asunto = String.format("Alerta: Incidencia #%d priorizada", notificacion.incidenciaId);
        String cuerpo = construirCuerpoEmail(notificacion);

        // Crear y enviar el email
        Mail email = new Mail()
            .setFrom("alertas@urban-events.local")
            .setTo(Collections.singletonList(notificacion.destinatario))
            .setSubject(asunto)
            .setText(cuerpo)
            .setHtml(construirHtmlEmail(notificacion));

        // Ejecutar de forma asincrónica manteniendo el contexto reactivo
        mailer.send(email)
            .chain(() -> {
                LOG.infof("Email enviado correctamente para notificación %d", notificacion.id);
                // Actualizar el estado a ENVIADA y publicar en Kafka
                return Panache.withTransaction(() -> 
                    Notificacion.<Notificacion>findById(notificacion.id)
                        .chain(notif -> {
                            if (notif == null) {
                                return Uni.createFrom().failure(new RuntimeException("Notificación no encontrada con ID: " + notificacion.id));
                            }
                            notif.contenido = cuerpo;
                            notif.estado = EstadoNotificacion.ENVIADA;
                            return notif.persist()
                                .chain(updated -> {
                                    LOG.infof("Notificación %d actualizada a estado ENVIADA", notificacion.id);
                                    // Publicar evento en Kafka después de enviar el email
                                    return publicarEventoNotificada(notif);
                                });
                        })
                );
            })
            .subscribe().with(
                v -> LOG.infof("Proceso de notificación completado para incidencia %d", notificacion.incidenciaId),
                failure -> {
                    LOG.errorf(failure, "Error al enviar email para notificación %d", notificacion.id);
                    // Actualizar el estado a ERROR
                    Panache.withTransaction(() -> 
                        Notificacion.<Notificacion>findById(notificacion.id)
                            .chain(notif -> {
                                if (notif != null) {
                                    notif.estado = EstadoNotificacion.ERROR;
                                    return notif.persist();
                                }
                                return Uni.createFrom().nullItem();
                            })
                    ).subscribe().with(
                        v -> LOG.infof("Notificación %d actualizada a estado ERROR", notificacion.id),
                        err -> LOG.errorf(err, "Error al actualizar estado ERROR de notificación %d", notificacion.id)
                    );
                }
            );
    }

    /**
     * Publica el evento de notificación en la cola de Kafka.
     */
    private Uni<Void> publicarEventoNotificada(Notificacion notificacion) {
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

    /**
     * Construye el contenido de texto plano del email.
     */
    private String construirCuerpoEmail(Notificacion notificacion) {
        return String.format(
            "Se ha detectado una alerta de alta prioridad.\n\n" +
            "Detalles:\n" +
            "- Incidencia ID: %d\n" +
            "- Canal: %s\n" +
            "- Estado: %s\n" +
            "- Fecha: %s\n\n" +
            "Por favor, revise la incidencia en el sistema de gestión.\n\n" +
            "---\n" +
            "Este es un correo automático. No responda a este mensaje.",
            notificacion.incidenciaId,
            notificacion.canal,
            notificacion.estado,
            notificacion.enviadaEn
        );
    }

    /**
     * Construye el contenido en formato HTML del email.
     */
    private String construirHtmlEmail(Notificacion notificacion) {
        return String.format(
            "<html>\n" +
            "<head><meta charset=\"UTF-8\"></head>\n" +
            "<body style=\"font-family: Arial, sans-serif;\">\n" +
            "<h2 style=\"color: #d32f2f;\">Alerta de Alta Prioridad</h2>\n" +
            "<p>Se ha detectado una alerta que requiere atención inmediata.</p>\n" +
            "<table border=\"1\" cellpadding=\"10\" style=\"border-collapse: collapse;\">\n" +
            "  <tr>\n" +
            "    <td style=\"font-weight: bold;\" bgcolor=\"#f5f5f5\">Incidencia ID</td>\n" +
            "    <td>%d</td>\n" +
            "  </tr>\n" +
            "  <tr>\n" +
            "    <td style=\"font-weight: bold;\" bgcolor=\"#f5f5f5\">Canal</td>\n" +
            "    <td>%s</td>\n" +
            "  </tr>\n" +
            "  <tr>\n" +
            "    <td style=\"font-weight: bold;\" bgcolor=\"#f5f5f5\">Estado</td>\n" +
            "    <td>%s</td>\n" +
            "  </tr>\n" +
            "  <tr>\n" +
            "    <td style=\"font-weight: bold;\" bgcolor=\"#f5f5f5\">Fecha</td>\n" +
            "    <td>%s</td>\n" +
            "  </tr>\n" +
            "</table>\n" +
            "<p style=\"margin-top: 20px; color: #666;\">\n" +
            "Por favor, revise la incidencia en el <a href=\"http://localhost:3000\" style=\"color: #1976d2;\">sistema de gestión</a>.\n" +
            "</p>\n" +
            "<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">\n" +
            "<p style=\"color: #999; font-size: 12px;\">\n" +
            "Este es un correo automático generado por Urban Events. No responda a este mensaje.\n" +
            "</p>\n" +
            "</body>\n" +
            "</html>",
            notificacion.incidenciaId,
            notificacion.canal,
            notificacion.estado,
            notificacion.enviadaEn
        );
    }
}

