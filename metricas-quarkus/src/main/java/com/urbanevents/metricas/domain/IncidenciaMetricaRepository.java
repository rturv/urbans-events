package com.urbanevents.metricas.domain;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repositorio Panache para IncidenciaMetrica.
 * Proporciona métodos reutilizables para acceso a datos.
 */
@ApplicationScoped
public class IncidenciaMetricaRepository implements PanacheRepository<IncidenciaMetrica> {

    /**
     * Busca una métrica de incidencia por su ID.
     */
    public IncidenciaMetrica findByIncidenciaId(Long incidenciaId) {
        return find("incidenciaId", incidenciaId).firstResult();
    }

    /**
     * Obtiene todas las incidencias pendientes.
     */
    public List<IncidenciaMetrica> findPendientes() {
        return find("estadoActual = ?1", EstadoIncidencia.PENDIENTE)
                .list();
    }

    /**
     * Obtiene todas las incidencias resueltas.
     */
    public List<IncidenciaMetrica> findResueltas() {
        return find("esResuelto = ?1", true)
                .list();
    }

    /**
     * Obtiene métricas de incidencia por tipo.
     */
    public List<IncidenciaMetrica> findByTipo(String tipo) {
        return find("tipoIncidencia", tipo)
                .list();
    }

    /**
     * Obtiene métricas de incidencia por tipo y prioridad.
     */
    public List<IncidenciaMetrica> findByTipoAndPrioridad(String tipo, String prioridad) {
        return find("tipoIncidencia = ?1 AND prioridad = ?2", tipo, prioridad)
                .list();
    }

    /**
     * Cuenta todas las incidencias.
     */
    public long countAll() {
        return count();
    }

    /**
     * Cuenta incidencias por tipo.
     */
    public long countByTipo(String tipo) {
        return count("tipoIncidencia", tipo);
    }

    /**
     * Cuenta incidencias resueltas.
     */
    public long countResueltas() {
        return count("esResuelto", true);
    }

    /**
     * Cuenta incidencias pendientes.
     */
    public long countPendientes() {
        return count("estadoActual", EstadoIncidencia.PENDIENTE);
    }

    /**
     * Obtiene todos los pares únicos de tipo y prioridad para recalcular agregaciones.
     */
    public Set<TipoPrioridadPar> findDistinctTipoAndPrioridad() {
        // Obtener todos y extraer pares únicos
        List<IncidenciaMetrica> todas = listAll();
        
        return todas.stream()
                .filter(m -> m.prioridad != null)
                .map(m -> new TipoPrioridadPar(m.tipoIncidencia, m.prioridad))
                .collect(Collectors.toSet());
    }

    /**
     * Record para representar un par tipo-prioridad.
     */
    public record TipoPrioridadPar(String tipo, String prioridad) {}
}
