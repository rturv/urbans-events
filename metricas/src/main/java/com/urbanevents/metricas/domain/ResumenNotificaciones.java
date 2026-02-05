package com.urbanevents.metricas.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "resumen_notificaciones")
public class ResumenNotificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resumen_notificaciones_seq")
    @SequenceGenerator(name = "resumen_notificaciones_seq", sequenceName = "resumen_notificaciones_seq", allocationSize = 1)
    private Long id;
    private long total;

    public ResumenNotificaciones() {
    }

    public ResumenNotificaciones(Long id) {
        this.id = id;
        this.total = 0;
    }

    public Long getId() {
        return id;
    }

    public long getTotal() {
        return total;
    }

    public void incrementar() {
        total++;
    }
}
