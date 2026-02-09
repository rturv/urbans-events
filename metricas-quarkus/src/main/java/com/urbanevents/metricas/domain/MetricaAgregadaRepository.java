package com.urbanevents.metricas.domain;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repositorio Panache para MetricaAgregada.
 * Proporciona métodos para acceso a datos agregados y resúmenes.
 */
@ApplicationScoped
public class MetricaAgregadaRepository implements PanacheRepository<MetricaAgregada> {

    /**
     * Busca una métrica agregada por tipo e incidencia.
     */
    public MetricaAgregada findByTipoAndPrioridad(String tipo, String prioridad) {
        return find("tipoIncidencia = ?1 AND prioridad = ?2", tipo, prioridad)
                .firstResult();
    }

    /**
     * Obtiene todas las métricas de un tipo específico.
     */
    public List<MetricaAgregada> findByTipo(String tipo) {
        return find("tipoIncidencia", tipo)
                .list();
    }

    /**
     * Obtiene todas las métricas agregadas ordenadas.
     */
    public List<MetricaAgregada> findAllOrdenadas() {
        return find("ORDER BY tipoIncidencia, prioridad")
                .list();
    }

    /**
     * Obtiene el total de incidencias agregadas.
     * Se suma el campo cantidadTotal de todas las métricas agregadas.
     */
    public Long getTotalIncidencias() {
        List<MetricaAgregada> todas = findAllOrdenadas();
        return todas.stream().mapToLong(m -> m.cantidadTotal != null ? m.cantidadTotal : 0).sum();
    }

    /**
     * Obtiene el total de incidencias resueltas.
     */
    public Long getTotalResueltas() {
        List<MetricaAgregada> todas = findAllOrdenadas();
        return todas.stream().mapToLong(m -> m.cantidadResuelta != null ? m.cantidadResuelta : 0).sum();
    }

    /**
     * Obtiene el total de incidencias pendientes.
     */
    public Long getTotalPendientes() {
        List<MetricaAgregada> todas = findAllOrdenadas();
        return todas.stream().mapToLong(m -> m.cantidadPendiente != null ? m.cantidadPendiente : 0).sum();
    }

    /**
     * Obtiene el total de incidencias rechazadas.
     */
    public Long getTotalRechazadas() {
        List<MetricaAgregada> todas = findAllOrdenadas();
        return todas.stream().mapToLong(m -> m.cantidadRechazada != null ? m.cantidadRechazada : 0).sum();
    }

    /**
     * Obtiene el tiempo promedio de resolución agregado.
     */
    public Double getPromedioResolucionSegregado() {
        List<MetricaAgregada> todas = findAllOrdenadas();
        return todas.stream()
                .filter(m -> m.tiempoPromedioResolucionSeg != null)
                .mapToDouble(m -> m.tiempoPromedioResolucionSeg)
                .average()
                .orElse(0.0);
    }
}
