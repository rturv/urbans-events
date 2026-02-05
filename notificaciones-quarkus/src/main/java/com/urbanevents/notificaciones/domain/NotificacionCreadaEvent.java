package com.urbanevents.notificaciones.domain;

/**
 * Evento de dominio disparado cuando se crea una nueva notificación.
 * Este evento es observado para realizar acciones asincrónicas como enviar emails.
 */
public class NotificacionCreadaEvent {
    
    private final Notificacion notificacion;

    public NotificacionCreadaEvent(Notificacion notificacion) {
        this.notificacion = notificacion;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }
}
