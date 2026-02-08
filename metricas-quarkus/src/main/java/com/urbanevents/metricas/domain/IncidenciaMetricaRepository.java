package com.urbanevents.metricas.domain;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

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
        return find("incidenciaId", incidenciaId).firstResult().await().indefinitely();
    }

    /**
     * Obtiene todas las incidencias pendientes.
     */
    public List<IncidenciaMetrica> findPendientes() {
        return find("estadoActual = ?1", EstadoIncidencia.PENDIENTE)
                .list()
                .await().indefinitely();
    }

    /**
     * Obtiene todas las incidencias resueltas.
     */
    public List<IncidenciaMetrica> findResueltas() {
        return find("esResuelto = ?1", true)
                .list()
                .await().indefinitely();
    }

    /**
     * Obtiene métricas de incidencia por tipo.
     */
    public List<IncidenciaMetrica> findByTipo(String tipo) {
        return find("tipoIncidencia", tipo)
                .list()
                .await().indefinitely();
    }

    /**
     * Obtiene métricas de incidencia por tipo y prioridad.
     */
    public List<IncidenciaMetrica> findByTipoAndPrioridad(String tipo, String prioridad) {
        return find("tipoIncidencia = ?1 AND prioridad = ?2", tipo, prioridad)
                .list()
                .await().indefinitely();
    }

    /**
     * Cuenta todas las incidencias.
     */
    public long countAll() {
        return count().await().indefinitely();
    }

    /**
     * Cuenta incidencias por tipo.
     */
    public long countByTipo(String tipo) {
        return count("tipoIncidencia", tipo).await().indefinitely();
    }

    /**
     * Cuenta incidencias resueltas.
     */
    public long countResueltas() {
        return count("esResuelto", true).await().indefinitely();
    }

    /**
     * Cuenta incidencias pendientes.
     */
    public long countPendientes() {
        return count("estadoActual", EstadoIncidencia.PENDIENTE).await().indefinitely();
    }
}
