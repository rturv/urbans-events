package com.urbanevents.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record IncidenciaNotificadaEvent(
        @Valid
        @NotNull
        EventMetadata metadata,

        @NotNull
        Long incidenciaId,

        @NotBlank
        @Size(max = 50)
        String canal,

        @NotBlank
        @Size(max = 255)
        String destinatario,

        @NotBlank
        @Size(max = 50)
        String estado,

        @NotNull
        Instant notificadaEn
) {
}
