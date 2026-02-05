package com.urbanevents.notificaciones.domain;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "notificaciones", schema = "notificaciones")
public class Notificacion extends PanacheEntity {
    
    @Column(name = "incidencia_id")
    public Long incidenciaId;
    
    public String canal;
    
    @Column(name = "destino")
    public String destinatario;
    
    public String estado;
    
    @Column(name = "creado")
    public Instant enviadaEn;
    
    public String contenido;
    
    @Column(name = "enviado")
    public Boolean enviado;

    public Notificacion() {
    }

    public Notificacion(Long incidenciaId, String canal, String destinatario, String estado, Instant enviadaEn) {
        this.incidenciaId = incidenciaId;
        this.canal = canal;
        this.destinatario = destinatario;
        this.estado = estado;
        this.enviadaEn = enviadaEn;
        this.contenido = "";
        this.enviado = false;
    }
}
