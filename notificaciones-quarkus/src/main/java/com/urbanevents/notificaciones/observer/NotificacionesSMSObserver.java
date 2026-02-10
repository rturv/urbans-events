package com.urbanevents.notificaciones.observer;

import com.urbanevents.notificaciones.domain.NotificacionCreadaEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

@ApplicationScoped
public class NotificacionesSMSObserver extends NotificacionObserverBase {
 
    private static final Logger LOG = Logger.getLogger(NotificacionesSMSObserver.class);

     /**
     * Observa el evento de creación de notificación.
     * Si el canal es "sms", procesa la notificación para enviar un SMS.
     *
     * @param evento El evento que contiene la notificación creada
     */

    public void onNofificacionPriorizada(@Observes NotificacionCreadaEvent evento) {

        if (!"sms".equalsIgnoreCase(evento.getNotificacion().canal)) {
            return; // Ignorar notificaciones que no sean para SMS
        }

        //Aqui vendria la llogica del SMS, por ahora solo escribimos en log
        LOG.infof("Notificación SMS creada para incidencia %d, destinatario: %s, contenido: %s",
                evento.getNotificacion().incidenciaId,
                evento.getNotificacion().destinatario,
                evento.getNotificacion().contenido);

        this.publicarEventoNotificada(evento.getNotificacion());
    }

}
