package com.urbanevents.registro.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incidencias")
public class Incidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incidencias_seq")
    @SequenceGenerator(name = "incidencias_seq", sequenceName = "registro_incidencias.incidencias_seq", allocationSize = 1)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String tipo;
    
    @Size(max = 2000)
    private String descripcion;
    
    @NotBlank
    @Size(max = 255)
    private String origen;
    
    @NotBlank
    @Size(max = 255)
    private String ubicacion;
    
    @Size(max = 50)
    private String estado;
    
    @Size(max = 50)
    private String prioridad;
    private Instant creadaEn;
    
    @ElementCollection
    @CollectionTable(name = "incidencia_comentarios", joinColumns = @jakarta.persistence.JoinColumn(name = "incidencia_id"))
    @Column(name = "comentario")
    private List<String> comentarios = new ArrayList<>();

    public Incidencia() {
    }

    public Incidencia(Long id, String tipo, String descripcion, String origen, String ubicacion, String estado, Instant creadaEn) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.origen = origen;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.prioridad = null;
        this.creadaEn = creadaEn;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getOrigen() {
        return origen;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public Instant getCreadaEn() {
        return creadaEn;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<String> comentarios) {
        this.comentarios = comentarios;
    }

    public void agregarComentario(String comentario) {
        if (this.comentarios == null) {
            this.comentarios = new ArrayList<>();
        }
        this.comentarios.add(comentario);
    }
}
