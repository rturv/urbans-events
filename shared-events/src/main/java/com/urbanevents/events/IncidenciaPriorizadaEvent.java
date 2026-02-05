package com.urbanevents.events;

import java.time.Instant;

public record IncidenciaPriorizadaEvent(
        EventMetadata metadata,
        Long incidenciaId,
        String prioridad,
        String motivo,
        Instant priorizadaEn
) {
}
