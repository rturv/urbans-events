package com.urbanevents.metricas.api;

import java.util.Map;

public record ResumenResponse(
        Map<String, Long> incidenciasPorTipo,
        Map<String, Long> prioridades,
        long notificacionesTotales
) {
}
