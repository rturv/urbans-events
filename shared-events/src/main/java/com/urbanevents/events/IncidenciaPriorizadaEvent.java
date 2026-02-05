package com.urbanevents.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record IncidenciaPriorizadaEvent(
        @Valid
        @NotNull
        EventMetadata metadata,

        @NotNull
        Long incidenciaId,

        @NotBlank
        @Size(max = 50)
        String prioridad,

        @Size(max = 255)
        String motivo,

        @NotNull
        Instant priorizadaEn
) {
}
