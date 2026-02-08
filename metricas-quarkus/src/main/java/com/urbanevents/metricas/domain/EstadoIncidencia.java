package com.urbanevents.metricas.domain;

/**
 * Estados posibles de una incidencia según los eventos que recibe.
 * Se actualizará cuando llegar un IncidenciaChangedEvent.
 */
public enum EstadoIncidencia {
    PENDIENTE,    // Incidencia creada pero sin resolver
    RESUELTO,     // Incidencia resuelta exitosamente
    CERRADO,      // Incidencia cerrada (puede ser resuelta o rechazada)
    RECHAZADO     // Incidencia rechazada (no se pudo resolver)
}
