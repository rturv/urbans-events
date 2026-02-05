package com.urbanevents.registro.api;

public record NuevaIncidenciaRequest(
        String tipo,
        String descripcion,
        String origen,
        String ubicacion
) {
}
