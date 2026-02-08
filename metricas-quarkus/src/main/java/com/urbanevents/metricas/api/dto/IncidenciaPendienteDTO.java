package com.urbanevents.metricas.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para incidencias pendientes activas.
 * Muestra incidencias que aún no se han resuelto y tiempo transcurrido.
 */
public class IncidenciaPendienteDTO {

    @NotNull
    public Long incidenciaId;

    @NotNull
    public String tipo;

    public String prioridad;

    @NotNull
    public String estado;

    // Tiempos en segundos
    public Long tiempoDesdeCreacionSeg;
    public Long tiempoDesdeCreacionMin;

    // Constructor vacío
    public IncidenciaPendienteDTO() {
    }

    public IncidenciaPendienteDTO(Long id, String tipo, String prioridad, String estado) {
        this.incidenciaId = id;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "IncidenciaPendienteDTO{" +
                "incidenciaId=" + incidenciaId +
                ", tipo='" + tipo + '\'' +
                ", tiempoDesdeCreacionSeg=" + tiempoDesdeCreacionSeg +
                '}';
    }
}
