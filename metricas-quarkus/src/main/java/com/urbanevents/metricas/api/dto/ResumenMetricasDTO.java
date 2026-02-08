package com.urbanevents.metricas.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para respuesta de resumen general de métricas.
 * Contiene totales y tasas agregadas de todas las incidencias.
 */
public class ResumenMetricasDTO {

    @NotNull
    public Long totalIncidencias;

    @NotNull
    public Long incidenciasResueltas;

    @NotNull
    public Long incidenciasPendientes;

    @NotNull
    public Long incidenciasRechazadas;

    // Tasas en porcentaje
    public Double tasaExitoPct;
    public Double tasaFracasoPct;
    public Double tasaPendientePct;

    // Tiempos en segundos
    public Double tiempoPromedioResolucionSeg;
    public Long tiempoPromedioResolucionMin;

    // Constructor
    public ResumenMetricasDTO() {
    }

    public ResumenMetricasDTO(Long total, Long resueltas, Long pendientes, Long rechazadas) {
        this.totalIncidencias = total;
        this.incidenciasResueltas = resueltas;
        this.incidenciasPendientes = pendientes;
        this.incidenciasRechazadas = rechazadas;
    }

    @Override
    public String toString() {
        return "ResumenMetricasDTO{" +
                "totalIncidencias=" + totalIncidencias +
                ", resueltas=" + incidenciasResueltas +
                ", pendientes=" + incidenciasPendientes +
                ", rechazadas=" + incidenciasRechazadas +
                ", tasaExito=" + tasaExitoPct +
                '}';
    }
}
