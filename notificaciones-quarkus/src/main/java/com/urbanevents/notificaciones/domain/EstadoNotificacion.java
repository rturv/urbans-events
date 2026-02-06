package com.urbanevents.notificaciones.domain;

/**
 * Estados posibles de una notificación.
 */
public enum EstadoNotificacion {
    /**
     * La notificación ha sido creada pero no se ha enviado.
     */
    NO_ENVIADA,
    
    /**
     * La notificación se ha enviado correctamente.
     */
    ENVIADA,
    
    /**
     * La notificación se ignora (ej: prioridad baja).
     */
    IGNORAR,
    
    /**
     * Hubo un error al enviar la notificación.
     */
    ERROR
}
