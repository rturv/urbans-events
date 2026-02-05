package com.urbanevents.notificaciones.domain;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "notificaciones", schema = "notificaciones")
public class Notificacion extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notificaciones_seq")
    @SequenceGenerator(name = "notificaciones_seq", sequenceName = "notificaciones.notificaciones_seq", allocationSize = 1)
    public Long id;

    @Column(name = "incidencia_id")
    public Long incidenciaId;
    
    @Size(max = 50)
    public String canal;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "destino")
    public String destinatario;
    
    @Size(max = 50)
    public String estado;
    
    @Column(name = "creado")
    public Instant enviadaEn;
    
    @Size(max = 4000)
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
