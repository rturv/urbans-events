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
public class IncidenciaMetrica {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incidencias_metricas_seq")
    @SequenceGenerator(name = "incidencias_metricas_seq", sequenceName = "metricas.incidencias_metricas_seq", allocationSize = 1)
    public Long id;

    @NotNull
    @Column(name = "incidencia_id", unique = true, nullable = false)
    public Long incidenciaId;

    @NotBlank
    @Column(name = "tipo_incidencia")
    public String tipoIncidencia;

    public String prioridad; // nullable hasta que llega IncidenciaPriorizadaEvent

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_actual")
    public EstadoIncidencia estadoActual = EstadoIncidencia.PENDIENTE;

    // Timestamps de eventos
    @NotNull
    @Column(name = "tiempo_creacion")
    public Instant tiempoCreacion;

    @Column(name = "tiempo_priorizacion")
    public Instant tiempoPriorizacion;
    @Column(name = "tiempo_notificacion")
    public Instant tiempoNotificacion;
    @Column(name = "tiempo_resolucion")
    public Instant tiempoResolucion;

    // Tiempos calculados en milisegundos
    @Column(name = "ms_priorizacion")
    public Long msPriorizacion;      // Desde creación hasta priorización
    @Column(name = "ms_notificacion")
    public Long msNotificacion;      // Desde priorización hasta notificación
    @Column(name = "ms_resolucion")
    public Long msResolucion;        // Desde creación hasta resolución

    // Banderas de estado
    public Boolean esResuelto = false; // true si estado es RESUELTO, CERRADO, RECHAZADO

    // Control
    @NotNull
    @Column(name = "ultima_actualizacion")
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
