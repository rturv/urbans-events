package com.urbanevents.registro.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NuevaIncidenciaRequest(
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
        String ubicacion
) {
}
