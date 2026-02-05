package com.urbanevents.priorizacion.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "prioridad_incidencia")
public class PrioridadIncidencia {
    @Id
    @NotNull
    private Long incidenciaId;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Prioridad prioridad;
    @Size(max = 255)
    private String motivo;
    private Instant actualizadaEn;

    public PrioridadIncidencia() {
    }

    public PrioridadIncidencia(Long incidenciaId, Prioridad prioridad, String motivo, Instant actualizadaEn) {
        this.incidenciaId = incidenciaId;
        this.prioridad = prioridad;
        this.motivo = motivo;
        this.actualizadaEn = actualizadaEn;
    }

    public Long getIncidenciaId() {
        return incidenciaId;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public String getMotivo() {
        return motivo;
    }

    public Instant getActualizadaEn() {
        return actualizadaEn;
    }
}
