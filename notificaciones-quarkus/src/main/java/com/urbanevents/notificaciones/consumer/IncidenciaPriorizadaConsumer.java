package com.urbanevents.notificaciones.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.notificaciones.domain.EstadoNotificacion;
import com.urbanevents.notificaciones.domain.Notificacion;
import com.urbanevents.notificaciones.domain.NotificacionCreadaEvent;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.time.Instant;

@ApplicationScoped
public class IncidenciaPriorizadaConsumer {

    private static final Logger LOG = Logger.getLogger(IncidenciaPriorizadaConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    Event<NotificacionCreadaEvent> notificacionEvent;

    @ConfigProperty(name = "notificaciones.destinatarios")
    String destinatarios;

    @Incoming("incidencias-priorizadas")
    public Uni<Void> onMessage(String eventJson) {
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

                Instant now = Instant.now();
                
                // Filtrar solo incidencias de alta prioridad para enviar email
                if (!"alta".equalsIgnoreCase(event.prioridad())) {
                    LOG.infof("Incidencia %s con prioridad %s no será notificada - guardando como IGNORAR", event.incidenciaId(), event.prioridad());
                    
                    // Guardar notificación con estado IGNORAR, sin canal, destino ni contenido
                    Notificacion notificacion = new Notificacion(event.incidenciaId(), null, null, EstadoNotificacion.IGNORAR, now);
                    
                    return Panache.withTransaction(() -> notificacion.persist())
                        .onItem().invoke(persisted -> {
                            LOG.infof("Notificación guardada para incidencia %s con estado IGNORAR", event.incidenciaId());
                        })
                        .replaceWithVoid();
                }

                String canal = "email";

                // Guardar notificación en la base de datos de forma reactiva con transacción
                Notificacion notificacion = new Notificacion(event.incidenciaId(), canal, destinatarios, EstadoNotificacion.NO_ENVIADA, now);
                
                return Panache.withTransaction(() -> notificacion.persist())
                    .onItem().invoke(persisted -> {
                        // Castear el resultado de persist a Notificacion
                        Notificacion notif = (Notificacion) persisted;
                        LOG.infof("Notificación guardada para incidencia %s con estado NO_ENVIADA", event.incidenciaId());
                        
                        // Disparar evento de dominio para que el observer envíe el email
                        notificacionEvent.fire(new NotificacionCreadaEvent(notif));
                        LOG.infof("Evento de notificación disparado para incidencia %s", event.incidenciaId());
                    })
                    .replaceWithVoid();
            })
            .onFailure().invoke(e -> LOG.errorf(e, "Error procesando mensaje"))
            .onFailure().recoverWithNull();
    }
}
