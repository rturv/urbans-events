package com.urbanevents.priorizacion.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "prioridad_incidencia")
public class PrioridadIncidencia {
    @Id
    private Long incidenciaId;
    private String prioridad;
    private String motivo;
    private Instant actualizadaEn;

    public PrioridadIncidencia() {
    }

    public PrioridadIncidencia(Long incidenciaId, String prioridad, String motivo, Instant actualizadaEn) {
        this.incidenciaId = incidenciaId;
        this.prioridad = prioridad;
        this.motivo = motivo;
        this.actualizadaEn = actualizadaEn;
    }

    public Long getIncidenciaId() {
        return incidenciaId;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public String getMotivo() {
        return motivo;
    }

    public Instant getActualizadaEn() {
        return actualizadaEn;
    }
}
