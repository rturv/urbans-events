package com.urbanevents.metricas.domain;

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
public class MetricaAgregada  {

    @Id
    @Column(name = "id")
     @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metricas_agregadas_seq")
    @SequenceGenerator(name = "metricas_agregadas_seq", sequenceName = "metricas.metricas_agregadas_seq", allocationSize = 1)
   
    public Long id;

    @NotBlank
    @Column(name = "tipo_incidencia")
    public String tipoIncidencia;

    @Column(name = "prioridad")
    public String prioridad; // nullable para agregar todas las prioridades

    // Conteos
    @NotNull
    @Column(name = "cantidad_total")
    public Long cantidadTotal = 0L;

    @NotNull
    @Column(name = "cantidad_resuelta")
    public Long cantidadResuelta = 0L;

    @NotNull
    @Column(name = "cantidad_pendiente")
    public Long cantidadPendiente = 0L;

    @NotNull
    @Column(name = "cantidad_rechazada")
    public Long cantidadRechazada = 0L;

    // Tiempos en segundos (promedios, min, max)
    @Column(name = "tiempo_promedio_resolucion_seg")
    public Double tiempoPromedioResolucionSeg;
    @Column(name = "tiempo_min_resolucion_seg")
    public Long tiempoMinResolucionSeg;
    @Column(name = "tiempo_max_resolucion_seg")
    public Long tiempoMaxResolucionSeg;

    // Percentiles en segundos (para gráficas)
    @Column(name = "percentil_50_seg")
    public Double percentil50Seg;  // p50 (mediana)
    @Column(name = "percentil_95_seg")
    public Double percentil95Seg;  // p95
    @Column(name = "percentil_99_seg")
    public Double percentil99Seg;  // p99

    // Tiempo promedio de priorización
    @Column(name = "tiempo_promedio_priorizacion_seg")
    public Double tiempoPromedioProblemaizacionSeg;

    // Tasas en porcentaje
    @Column(name = "tasa_exito_pct")
    public Double tasaExitoPct;     // % resuelta
    @Column(name = "tasa_fracaso_pct")
    public Double tasaFracasoPct;   // % rechazada
    @Column(name = "tasa_pendiente_pct")
    public Double tasaPendientePct; // % pendiente

    // Control
    @NotNull
    @Column(name = "fecha_actualizacion")
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
