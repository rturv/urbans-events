package com.urbanevents.notificaciones.observer;

import com.urbanevents.notificaciones.domain.Notificacion;
import com.urbanevents.notificaciones.domain.NotificacionCreadaEvent;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Collections;

/**
 * Observador que escucha eventos de creación de notificaciones y envía emails.
 * Implementa el patrón Observer para desacoplar la lógica de persistencia de la lógica de envío de emails.
 */
@ApplicationScoped
public class NotificacionObserver {

    private static final Logger LOG = Logger.getLogger(NotificacionObserver.class);

    @Inject
    ReactiveMailer mailer;

    /**
     * Observa el evento de creación de notificación.
     * Si el email se envía correctamente, actualiza la notificación con el contenido y la marca como enviada.
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
            .chain(() -> Panache.withTransaction(() -> 
                Notificacion.<Notificacion>findById(notificacion.id)
                    .chain(notif -> {
                        if (notif == null) {
                            return Uni.createFrom().failure(new RuntimeException("Notificación no encontrada con ID: " + notificacion.id));
                        }
                        notif.contenido = cuerpo;
                        notif.enviado = true;
                        return notif.persist();
                    })
            ))
            .subscribe().with(
                v -> LOG.infof("Notificación %d actualizada como enviada en BBDD", notificacion.id),
                failure -> LOG.errorf(failure, "Error al enviar email o actualizar notificación %d", notificacion.id)
            );
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

