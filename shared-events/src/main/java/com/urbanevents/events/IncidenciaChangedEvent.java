package com.urbanevents.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record IncidenciaChangedEvent(
        @Valid
        @NotNull
        EventMetadata metadata,

        @NotNull
        Long incidenciaId,

        @NotNull
        @Size(max = 100)
        String nuevoEstado,

        @Size(max = 2000)
        String comentario,

        @NotNull
        Instant cambiadoEn
) {
}
