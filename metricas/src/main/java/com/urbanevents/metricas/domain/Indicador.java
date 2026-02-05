package com.urbanevents.metricas.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "indicadores")
public class Indicador {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicadores_seq")
    @SequenceGenerator(name = "indicadores_seq", sequenceName = "indicadores_seq", allocationSize = 1)
    private Long id;
    private String tipo;
    private long total;
    private long prioridadAlta;
    private long prioridadMedia;
    private long prioridadBaja;

    public Indicador() {
    }

    public Indicador(Long id, String tipo) {
        this.id = id;
        this.tipo = tipo;
        this.total = 0;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public long getTotal() {
        return total;
    }

    public long getPrioridadAlta() {
        return prioridadAlta;
    }

    public long getPrioridadMedia() {
        return prioridadMedia;
    }

    public long getPrioridadBaja() {
        return prioridadBaja;
    }

    public void incrementarTotal() {
        total++;
    }

    public void incrementarPrioridad(String prioridad) {
        if ("alta".equalsIgnoreCase(prioridad)) {
            prioridadAlta++;
        } else if ("baja".equalsIgnoreCase(prioridad)) {
            prioridadBaja++;
        } else {
            prioridadMedia++;
        }
    }
}
