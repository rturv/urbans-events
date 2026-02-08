package com.urbanevents.metricas.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * DTO para respuesta de métricas de una incidencia específica.
 * Contiene todos los tiempos y estados de una incidencia en particular.
 */
public class MetricaIncidenciaDTO {

    @NotNull
    public Long incidenciaId;

    @NotNull
    public String tipo;

    public String prioridad;

    @NotNull
    public String estado;

    // Timestamps (para referencia)
    public Instant tiempoCreacionUTC;
    public Instant tiempoResolucionUTC;

    // Tiempos en segundos
    public Long tiempoTotalResolucionSeg;
    public Long tiempoDesdeCreacionAPriorizacionSeg;
    public Long tiempoDesdeCreacionANotificacionSeg;
    public Long tiempoDesdeCreacionAResolucionSeg;

    // Constructor vacío
    public MetricaIncidenciaDTO() {
    }

    public MetricaIncidenciaDTO(Long id, String tipo, String prioridad, String estado) {
        this.incidenciaId = id;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "MetricaIncidenciaDTO{" +
                "incidenciaId=" + incidenciaId +
                ", tipo='" + tipo + '\'' +
                ", estado='" + estado + '\'' +
                ", tiempoTotalSeg=" + tiempoTotalResolucionSeg +
                '}';
    }
}
