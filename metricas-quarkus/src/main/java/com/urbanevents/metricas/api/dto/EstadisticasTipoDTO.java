package com.urbanevents.metricas.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para estadísticas agrupadas por tipo de incidencia.
 * Resumen de un tipo sin desglosar por prioridad.
 */
public class EstadisticasTipoDTO {

    @NotNull
    public String tipo;

    @NotNull
    public Long cantidad;

    public Long cantidadResuelta;
    public Long cantidadPendiente;
    public Long cantidadRechazada;

    public Double tasaExitoPct;
    public Double tiempoPromedioResolucionSeg;
    public Long tiempoMinResolucionSeg;
    public Long tiempoMaxResolucionSeg;

    // Constructor vacío
    public EstadisticasTipoDTO() {
    }

    public EstadisticasTipoDTO(String tipo, Long cantidad) {
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "EstadisticasTipoDTO{" +
                "tipo='" + tipo + '\'' +
                ", cantidad=" + cantidad +
                ", tasaExito=" + tasaExitoPct +
                '}';
    }
}
