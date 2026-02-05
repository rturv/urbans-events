package com.urbanevents.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record IncidenciaCreadaEvent(
        @Valid
        @NotNull
        EventMetadata metadata,

        @NotNull
        Long incidenciaId,

        @NotBlank
        @Size(max = 100)
        String tipo,

        @Size(max = 2000)
        String descripcion,

        @NotBlank
        @Size(max = 255)
        String origen,

        @NotBlank
        @Size(max = 255)
        String ubicacion,

        @NotNull
        Instant creadaEn
) {
}
