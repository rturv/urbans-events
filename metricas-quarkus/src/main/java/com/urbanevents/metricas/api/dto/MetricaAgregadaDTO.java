package com.urbanevents.metricas.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para respuesta de métricas agregadas (resumen por tipo y prioridad).
 * Contiene todos los datos necesarios para construir gráficas y dashboards.
 */
public class MetricaAgregadaDTO {

    @NotNull
    public String tipoIncidencia;

    public String prioridad;

    // Conteos
    @NotNull
    public Long cantidadTotal;

    public Long cantidadResuelta;
    public Long cantidadPendiente;
    public Long cantidadRechazada;

    // Tasas en porcentaje
    public Double tasaExitoPct;
    public Double tasaFracasoPct;
    public Double tasaPendientePct;

    // Tiempos en segundos
    public Double tiempoPromedioResolucionSeg;
    public Long tiempoMinResolucionSeg;
    public Long tiempoMaxResolucionSeg;

    // Percentiles
    public Double p50Seg;
    public Double p95Seg;
    public Double p99Seg;

    // Tiempo promedio de priorización
    public Double tiempoPromedioProblemaizacionSeg;

    // Constructor vacío
    public MetricaAgregadaDTO() {
    }

    public MetricaAgregadaDTO(String tipo, String prioridad, Long total) {
        this.tipoIncidencia = tipo;
        this.prioridad = prioridad;
        this.cantidadTotal = total;
    }

    @Override
    public String toString() {
        return "MetricaAgregadaDTO{" +
                "tipo='" + tipoIncidencia + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", cantidadTotal=" + cantidadTotal +
                ", tasaExito=" + tasaExitoPct +
                ", tiempoPromedio=" + tiempoPromedioResolucionSeg +
                '}';
    }
}
