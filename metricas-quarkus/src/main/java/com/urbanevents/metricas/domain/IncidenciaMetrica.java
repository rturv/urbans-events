package com.urbanevents.metricas.domain;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Entidad que almacena métricas de una incidencia específica.
 * Se actualiza conforme llegan eventos de Kafka sobre esa incidencia.
 * 
 * Solo almacena el ESTADO ACTUAL (no histórico).
 */
@Entity
@Table(name = "incidencias_metricas", schema = "metricas")
public class IncidenciaMetrica extends PanacheEntity {

    @NotNull
    @Column(unique = true)
    public Long incidenciaId;

    @NotBlank
    public String tipoIncidencia;

    public String prioridad; // nullable hasta que llega IncidenciaPriorizadaEvent

    @Enumerated(EnumType.STRING)
    public EstadoIncidencia estadoActual = EstadoIncidencia.PENDIENTE;

    // Timestamps de eventos
    @NotNull
    public Instant tiempoCreacion;

    public Instant tiempoPriorizacion;
    public Instant tiempoNotificacion;
    public Instant tiempoResolucion;

    // Tiempos calculados en milisegundos
    public Long msPriorizacion;      // Desde creación hasta priorización
    public Long msNotificacion;      // Desde priorización hasta notificación
    public Long msResolucion;        // Desde creación hasta resolución

    // Banderas de estado
    public Boolean esResuelto = false; // true si estado es RESUELTO, CERRADO, RECHAZADO

    // Control
    @NotNull
    public Instant ultimaActualizacion;

    // Constructores
    public IncidenciaMetrica() {
    }

    public IncidenciaMetrica(Long incidenciaId, String tipoIncidencia, Instant tiempoCreacion) {
        this.incidenciaId = incidenciaId;
        this.tipoIncidencia = tipoIncidencia;
        this.tiempoCreacion = tiempoCreacion;
        this.ultimaActualizacion = Instant.now();
    }

    @Override
    public String toString() {
        return "IncidenciaMetrica{" +
                "incidenciaId=" + incidenciaId +
                ", tipoIncidencia='" + tipoIncidencia + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", estadoActual=" + estadoActual +
                ", tiempoResolucion=" + tiempoResolucion +
                ", msResolucion=" + msResolucion +
                ", esResuelto=" + esResuelto +
                '}';
    }
}
