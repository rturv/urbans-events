package com.urbanevents.metricas.service;

import com.urbanevents.metricas.domain.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que recalcula las métricas agregadas.
 * Se ejecuta cada vez que cambia una incidencia para mantener
 * las estadísticas siempre actualizadas.
 */
@ApplicationScoped
public class AgregacionMetricasService {

    @Inject
    IncidenciaMetricaRepository incidenciaMetricaRepository;

    @Inject
    MetricaAgregadaRepository metricaAgregadaRepository;

    @Inject
    CalculoMetricasService calculoMetricasService;

    /**
     * Recalcula las métricas agregadas para un tipo e incidencia específicos.
     * Se llama después de procesar cada evento de cambio.
     */
    public void recalcularPorTipoYPrioridad(String tipo, String prioridad) {
        // Obtener o crear la métrica agregada
        MetricaAgregada agregada = metricaAgregadaRepository.findByTipoAndPrioridad(tipo, prioridad);
        if (agregada == null) {
            agregada = new MetricaAgregada(tipo, prioridad);
        }

        // Obtener todas las incidencias de este tipo y prioridad
        List<IncidenciaMetrica> incidencias = incidenciaMetricaRepository.findByTipoAndPrioridad(tipo, prioridad);

        // Calcular conteos
        long total = incidencias.size();
        long resuelta = incidencias.stream()
                .filter(i -> i.estadoActual == EstadoIncidencia.RESUELTO ||
                             i.estadoActual == EstadoIncidencia.CERRADO)
                .count();
        long rechazada = incidencias.stream()
                .filter(i -> i.estadoActual == EstadoIncidencia.RECHAZADO)
                .count();
        long pendiente = incidencias.stream()
                .filter(i -> i.estadoActual == EstadoIncidencia.PENDIENTE)
                .count();

        agregada.cantidadTotal = total;
        agregada.cantidadResuelta = resuelta;
        agregada.cantidadRechazada = rechazada;
        agregada.cantidadPendiente = pendiente;

        // Calcular tiempos para incidencias resueltas
        List<IncidenciaMetrica> resueltas = incidencias.stream()
                .filter(i -> i.msResolucion != null && i.msResolucion > 0)
                .collect(Collectors.toList());

        if (!resueltas.isEmpty()) {
            // Tiempo promedio en segundos
            long sumaMs = resueltas.stream().mapToLong(i -> i.msResolucion).sum();
            double promedioSeg = calculoMetricasService.msASegundosDouble(sumaMs / resueltas.size());
            agregada.tiempoPromedioResolucionSeg = promedioSeg;

            // Min y max
            long minMs = resueltas.stream().mapToLong(i -> i.msResolucion).min().orElse(0);
            long maxMs = resueltas.stream().mapToLong(i -> i.msResolucion).max().orElse(0);
            agregada.tiempoMinResolucionSeg = calculoMetricasService.msASegundos(minMs);
            agregada.tiempoMaxResolucionSeg = calculoMetricasService.msASegundos(maxMs);

            // Percentiles (p50, p95, p99)
            List<Long> tiemposOrdenados = resueltas.stream()
                    .map(i -> i.msResolucion)
                    .sorted()
                    .collect(Collectors.toList());

            agregada.percentil50Seg = calculoMetricasService.msASegundosDouble(
                    percentil(tiemposOrdenados, 50)
            );
            agregada.percentil95Seg = calculoMetricasService.msASegundosDouble(
                    percentil(tiemposOrdenados, 95)
            );
            agregada.percentil99Seg = calculoMetricasService.msASegundosDouble(
                    percentil(tiemposOrdenados, 99)
            );
        }

        // Calcular tiempo promedio de priorización
        List<IncidenciaMetrica> priorizadas = incidencias.stream()
                .filter(i -> i.msPriorizacion != null && i.msPriorizacion > 0)
                .collect(Collectors.toList());

        if (!priorizadas.isEmpty()) {
            long sumaMs = priorizadas.stream().mapToLong(i -> i.msPriorizacion).sum();
            double promedioSeg = calculoMetricasService.msASegundosDouble(sumaMs / priorizadas.size());
            agregada.tiempoPromedioProblemaizacionSeg = promedioSeg;
        }

        // Calcular tasas de éxito/fracaso/pendiente
        agregada.tasaExitoPct = calculoMetricasService.calcularTasaExito(resuelta, total);
        agregada.tasaFracasoPct = calculoMetricasService.calcularTasaFracaso(rechazada, total);
        agregada.tasaPendientePct = calculoMetricasService.calcularTasaPendiente(pendiente, total);

        // Actualizar fecha
        agregada.fechaActualizacion = Instant.now();

        // Guardar (Panache maneja automáticamente persist vs update)
        metricaAgregadaRepository.persist(agregada).await().indefinitely();
    }

    /**
     * Calcula un percentil de una lista de números.
     * Por ejemplo, percentil(lista, 95) retorna el valor del p95.
     */
    private long percentil(List<Long> valores, int percentil) {
        if (valores.isEmpty()) {
            return 0;
        }
        if (valores.size() == 1) {
            return valores.get(0);
        }

        // Fórmula: índice = (percentil / 100) * (n - 1)
        double indice = (percentil / 100.0) * (valores.size() - 1);
        int lower = (int) Math.floor(indice);
        int upper = (int) Math.ceil(indice);

        if (lower == upper) {
            return valores.get(lower);
        }

        // Interpolación lineal
        double fraccion = indice - lower;
        long v1 = valores.get(lower);
        long v2 = valores.get(upper);
        return (long) (v1 + fraccion * (v2 - v1));
    }
}
