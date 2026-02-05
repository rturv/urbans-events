package com.urbanevents.events;

import java.time.Instant;

public record IncidenciaNotificadaEvent(
        EventMetadata metadata,
        Long incidenciaId,
        String canal,
        String destinatario,
        String estado,
        Instant notificadaEn
) {
}
