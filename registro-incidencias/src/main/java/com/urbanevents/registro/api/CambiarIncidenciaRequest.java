package com.urbanevents.registro.api;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarIncidenciaRequest(
        @NotBlank(message = "El estado es requerido")
        @Size(max = 100, message = "El estado no puede exceder 100 caracteres")
        String nuevoEstado,

        @Nullable
        @Size(max = 2000, message = "El comentario no puede exceder 2000 caracteres")
        String comentario
) {
}
