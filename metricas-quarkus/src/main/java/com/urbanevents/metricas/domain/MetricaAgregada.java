package com.urbanevents.metricas.domain;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Entidad que almacena métricas AGREGADAS (resumen por tipo y prioridad).
 * Se recalcula cada vez que llega un evento de cambio de estado de incidencia.
 * 
 * Contiene promedios, percentiles, tasas y conteos para análisis y gráficas.
 */
@Entity
@Table(name = "metricas_agregadas", schema = "metricas", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"tipo_incidencia", "prioridad"})})
public class MetricaAgregada extends PanacheEntity {

    @NotBlank
    public String tipoIncidencia;

    public String prioridad; // nullable para agregar todas las prioridades

    // Conteos
    @NotNull
    public Long cantidadTotal = 0L;

    @NotNull
    public Long cantidadResuelta = 0L;

    @NotNull
    public Long cantidadPendiente = 0L;

    @NotNull
    public Long cantidadRechazada = 0L;

    // Tiempos en segundos (promedios, min, max)
    public Double tiempoPromedioResolucionSeg;
    public Long tiempoMinResolucionSeg;
    public Long tiempoMaxResolucionSeg;

    // Percentiles en segundos (para gráficas)
    public Double percentil50Seg;  // p50 (mediana)
    public Double percentil95Seg;  // p95
    public Double percentil99Seg;  // p99

    // Tiempo promedio de priorización
    public Double tiempoPromedioProblemaizacionSeg;

    // Tasas en porcentaje
    public Double tasaExitoPct;     // % resuelta
    public Double tasaFracasoPct;   // % rechazada
    public Double tasaPendientePct; // % pendiente

    // Control
    @NotNull
    public Instant fechaActualizacion;

    // Constructores
    public MetricaAgregada() {
    }

    public MetricaAgregada(String tipoIncidencia, String prioridad) {
        this.tipoIncidencia = tipoIncidencia;
        this.prioridad = prioridad;
        this.cantidadTotal = 0L;
        this.cantidadResuelta = 0L;
        this.cantidadPendiente = 0L;
        this.cantidadRechazada = 0L;
        this.fechaActualizacion = Instant.now();
    }

    @Override
    public String toString() {
        return "MetricaAgregada{" +
                "tipoIncidencia='" + tipoIncidencia + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", cantidadTotal=" + cantidadTotal +
                ", cantidadResuelta=" + cantidadResuelta +
                ", cantidadPendiente=" + cantidadPendiente +
                ", tiempoPromedioResolucionSeg=" + tiempoPromedioResolucionSeg +
                ", tasaExitoPct=" + tasaExitoPct +
                '}';
    }
}
