package com.urbanevents.notificaciones.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.time.Instant;

@Entity
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notificaciones_seq")
    @SequenceGenerator(name = "notificaciones_seq", sequenceName = "notificaciones.notificaciones_seq", allocationSize = 1)
    private Long id;
    private Long incidenciaId;
    private String canal;
    @Column(name = "destino")
    private String destinatario;
    private String estado;
    @Column(name = "creado")
    private Instant enviadaEn;
    private String contenido;
    @Column(name = "enviado")
    private Boolean enviado;

    public Notificacion() {
    }

    public Notificacion(Long id, Long incidenciaId, String canal, String destinatario, String estado, Instant enviadaEn) {
        this.id = id;
        this.incidenciaId = incidenciaId;
        this.canal = canal;
        this.destinatario = destinatario;
        this.estado = estado;
        this.enviadaEn = enviadaEn;
        this.contenido = ""; // o algún valor por defecto
        this.enviado = false; // por defecto
    }

    public Long getId() {
        return id;
    }

    public Long getIncidenciaId() {
        return incidenciaId;
    }

    public String getCanal() {
        return canal;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getEstado() {
        return estado;
    }

    public Instant getEnviadaEn() {
        return enviadaEn;
    }
}
