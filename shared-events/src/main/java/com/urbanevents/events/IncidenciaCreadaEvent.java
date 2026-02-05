package com.urbanevents.events;

import java.time.Instant;

public record IncidenciaCreadaEvent(
        EventMetadata metadata,
        Long incidenciaId,
        String tipo,
        String descripcion,
        String origen,
        String ubicacion,
        Instant creadaEn
) {
}
