package com.urbanevents.metricas.api;

import com.urbanevents.metricas.api.dto.*;
import com.urbanevents.metricas.domain.*;
import com.urbanevents.metricas.service.CalculoMetricasService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST para consultar métricas de incidencias.
 * Expone 6 endpoints para diferentes vistas y análisis.
 */
@Path("/api/metricas")
@Produces(MediaType.APPLICATION_JSON)
public class MetricasResource {

    private static final Logger LOG = Logger.getLogger(MetricasResource.class);

    @Inject
    IncidenciaMetricaRepository incidenciaMetricaRepository;

    @Inject
    MetricaAgregadaRepository metricaAgregadaRepository;

    @Inject
    CalculoMetricasService calculoMetricasService;

    /**
     * ENDPOINT 1: GET /api/metricas/resumen
     * Retorna un resumen general de todas las métricas.
     */
    @GET
    @Path("/resumen")
    @Transactional
    public ResumenMetricasDTO obtenerResumen() {
        LOG.info("Obteniendo resumen general de métricas");

        try {
            Long totalIncidencias = metricaAgregadaRepository.getTotalIncidencias();
            Long resuelta = metricaAgregadaRepository.getTotalResueltas();
            Long pendiente = metricaAgregadaRepository.getTotalPendientes();
            Long rechazada = metricaAgregadaRepository.getTotalRechazadas();

            ResumenMetricasDTO resumen = new ResumenMetricasDTO(
                    totalIncidencias,
                    resuelta,
                    pendiente,
                    rechazada
            );

            // Calcular tasas
            if (totalIncidencias > 0) {
                resumen.tasaExitoPct = calculoMetricasService.calcularTasaExito(resuelta, totalIncidencias);
                resumen.tasaFracasoPct = calculoMetricasService.calcularTasaFracaso(rechazada, totalIncidencias);
                resumen.tasaPendientePct = calculoMetricasService.calcularTasaPendiente(pendiente, totalIncidencias);
            }

            // Tiempo promedio global
            Double promedioSeg = metricaAgregadaRepository.getPromedioResolucionSegregado();
            resumen.tiempoPromedioResolucionSeg = promedioSeg;
            if (promedioSeg != null) {
                resumen.tiempoPromedioResolucionMin = Math.round(promedioSeg / 60);
            }

            LOG.infof("Resumen obtenido: %d incidencias totales, %d resueltas", totalIncidencias, resuelta);
            return resumen;

        } catch (Exception e) {
            LOG.errorf("Error obteniendo resumen: %s", e.getMessage());
            throw new WebApplicationException("Error obteniendo métricas", 500);
        }
    }

    /**
     * ENDPOINT 2: GET /api/metricas/agregadas?tipo=&prioridad=
     * Retorna métricas agregadas por tipo y prioridad (con filtros opcionales).
     */
    @GET
    @Path("/agregadas")
    @Transactional
    public List<MetricaAgregadaDTO> obtenerAgregadas(
            @QueryParam("tipo") String tipo,
            @QueryParam("prioridad") String prioridad) {

        LOG.infof("Obteniendo métricas agregadas: tipo=%s, prioridad=%s", tipo, prioridad);

        try {
            List<MetricaAgregada> metricas;

            if (tipo != null && prioridad != null) {
                // Ambos filtros
                MetricaAgregada m = metricaAgregadaRepository.findByTipoAndPrioridad(tipo, prioridad);
                metricas = m != null ? List.of(m) : List.of();
            } else if (tipo != null) {
                // Solo tipo
                metricas = metricaAgregadaRepository.findByTipo(tipo);
            } else if (prioridad != null) {
                // Solo prioridad
                metricas = metricaAgregadaRepository.findAllOrdenadas().stream()
                        .filter(m -> prioridad.equals(m.prioridad))
                        .collect(Collectors.toList());
            } else {
                // Sin filtros
                metricas = metricaAgregadaRepository.findAllOrdenadas();
            }

            // Convertir a DTOs
            return metricas.stream().map(this::convertirADTO).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.errorf("Error obteniendo agregadas: %s", e.getMessage());
            throw new WebApplicationException("Error obteniendo métricas agregadas", 500);
        }
    }

    /**
     * ENDPOINT 3: GET /api/metricas/tipo/{tipo}
     * Retorna estadísticas filtradas por tipo de incidencia.
     */
    @GET
    @Path("/tipo/{tipo}")
    @Transactional
    public List<MetricaAgregadaDTO> obtenerPorTipo(@PathParam("tipo") String tipo) {
        LOG.infof("Obteniendo métricas por tipo: %s", tipo);

        try {
            List<MetricaAgregada> metricas = metricaAgregadaRepository.findByTipo(tipo);
            return metricas.stream().map(this::convertirADTO).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.errorf("Error obteniendo por tipo: %s", e.getMessage());
            throw new WebApplicationException("Error obteniendo métricas por tipo", 500);
        }
    }

    /**
     * ENDPOINT 4: GET /api/metricas/incidencia/{incidenciaId}
     * Retorna métricas detalladas de una incidencia específica.
     */
    @GET
    @Path("/incidencia/{incidenciaId}")
    @Transactional
    public MetricaIncidenciaDTO obtenerPorIncidencia(@PathParam("incidenciaId") Long incidenciaId) {
        LOG.infof("Obteniendo métricas de incidencia: %d", incidenciaId);

        try {
            IncidenciaMetrica metrica = incidenciaMetricaRepository.findByIncidenciaId(incidenciaId);
            
            if (metrica == null) {
                throw new WebApplicationException("Incidencia no encontrada", 404);
            }

            return convertirADTO(metrica);

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOG.errorf("Error obteniendo incidencia: %s", e.getMessage());
            throw new WebApplicationException("Error obteniendo métrica de incidencia", 500);
        }
    }

    /**
     * ENDPOINT 5: GET /api/metricas/estadisticas-por-tipo
     * Retorna estadísticas agrupadas por tipo (sin desglosar prioridad).
     */
    @GET
    @Path("/estadisticas-por-tipo")
    @Transactional
    public List<EstadisticasTipoDTO> obtenerEstadisticasPorTipo() {
        LOG.info("Obteniendo estadísticas por tipo");

        try {
            // Obtener todas las métricas agregadas
            List<MetricaAgregada> todas = metricaAgregadaRepository.findAllOrdenadas();

            // Agrupar por tipo (ignorar prioridad)
            Map<String, List<MetricaAgregada>> porTipo = todas.stream()
                    .collect(Collectors.groupingBy(m -> m.tipoIncidencia));

            // Crear DTOs con agregación por tipo
            return porTipo.entrySet().stream().map(entry -> {
                String tipo = entry.getKey();
                List<MetricaAgregada> metricas = entry.getValue();

                EstadisticasTipoDTO dto = new EstadisticasTipoDTO(tipo, 0L);

                // Sumar conteos
                dto.cantidad = metricas.stream()
                        .mapToLong(m -> m.cantidadTotal != null ? m.cantidadTotal : 0)
                        .sum();
                dto.cantidadResuelta = metricas.stream()
                        .mapToLong(m -> m.cantidadResuelta != null ? m.cantidadResuelta : 0)
                        .sum();
                dto.cantidadPendiente = metricas.stream()
                        .mapToLong(m -> m.cantidadPendiente != null ? m.cantidadPendiente : 0)
                        .sum();
                dto.cantidadRechazada = metricas.stream()
                        .mapToLong(m -> m.cantidadRechazada != null ? m.cantidadRechazada : 0)
                        .sum();

                // Calcular tasas
                if (dto.cantidad > 0) {
                    dto.tasaExitoPct = calculoMetricasService.calcularTasaExito(
                            dto.cantidadResuelta, dto.cantidad
                    );
                }

                // Tiempo promedio (media de promedios)
                double sumaTiempos = metricas.stream()
                        .filter(m -> m.tiempoPromedioResolucionSeg != null)
                        .mapToDouble(m -> m.tiempoPromedioResolucionSeg)
                        .sum();
                if (!metricas.isEmpty()) {
                    dto.tiempoPromedioResolucionSeg = sumaTiempos / metricas.size();
                }

                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.errorf("Error obteniendo estadísticas por tipo: %s", e.getMessage());
            throw new WebApplicationException("Error obteniendo estadísticas", 500);
        }
    }

    /**
     * ENDPOINT 6: GET /api/metricas/pendientes
     * Retorna incidencias aún pendientes con tiempo transcurrido.
     */
    @GET
    @Path("/pendientes")
    @Transactional
    public List<IncidenciaPendienteDTO> obtenerPendientes() {
        LOG.info("Obteniendo incidencias pendientes");

        try {
            List<IncidenciaMetrica> pendientes = incidenciaMetricaRepository.findPendientes();

            Instant ahora = Instant.now();

            return pendientes.stream().map(m -> {
                IncidenciaPendienteDTO dto = new IncidenciaPendienteDTO(
                        m.incidenciaId,
                        m.tipoIncidencia,
                        m.prioridad,
                        m.estadoActual.toString()
                );

                // Calcular tiempo desde creación
                long msDesdeCreacion = calculoMetricasService.calcularTiempoEnMs(m.tiempoCreacion, ahora);
                dto.tiempoDesdeCreacionSeg = calculoMetricasService.msASegundos(msDesdeCreacion);
                dto.tiempoDesdeCreacionMin = dto.tiempoDesdeCreacionSeg / 60;

                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.errorf("Error obteniendo pendientes: %s", e.getMessage());
            throw new WebApplicationException("Error obteniendo incidencias pendientes", 500);
        }
    }

    // ================== MÉTODOS AUXILIARES ==================

    /**
     * Convierte una entidad IncidenciaMetrica a su DTO correspondiente.
     */
    private MetricaIncidenciaDTO convertirADTO(IncidenciaMetrica metrica) {
        MetricaIncidenciaDTO dto = new MetricaIncidenciaDTO(
                metrica.incidenciaId,
                metrica.tipoIncidencia,
                metrica.prioridad,
                metrica.estadoActual.toString()
        );

        dto.tiempoCreacionUTC = metrica.tiempoCreacion;
        dto.tiempoResolucionUTC = metrica.tiempoResolucion;

        // Convertir tiempos a segundos
        if (metrica.msResolucion != null) {
            dto.tiempoTotalResolucionSeg = calculoMetricasService.msASegundos(metrica.msResolucion);
        }
        if (metrica.msPriorizacion != null) {
            dto.tiempoDesdeCreacionAPriorizacionSeg = calculoMetricasService.msASegundos(metrica.msPriorizacion);
        }
        if (metrica.msNotificacion != null) {
            dto.tiempoDesdeCreacionANotificacionSeg = calculoMetricasService.msASegundos(metrica.msNotificacion);
        }
        if (metrica.msResolucion != null) {
            dto.tiempoDesdeCreacionAResolucionSeg = calculoMetricasService.msASegundos(metrica.msResolucion);
        }

        return dto;
    }

    /**
     * Convierte una entidad MetricaAgregada a su DTO correspondiente.
     */
    private MetricaAgregadaDTO convertirADTO(MetricaAgregada agregada) {
        MetricaAgregadaDTO dto = new MetricaAgregadaDTO(
                agregada.tipoIncidencia,
                agregada.prioridad,
                agregada.cantidadTotal
        );

        dto.cantidadResuelta = agregada.cantidadResuelta;
        dto.cantidadPendiente = agregada.cantidadPendiente;
        dto.cantidadRechazada = agregada.cantidadRechazada;

        dto.tasaExitoPct = agregada.tasaExitoPct;
        dto.tasaFracasoPct = agregada.tasaFracasoPct;
        dto.tasaPendientePct = agregada.tasaPendientePct;

        dto.tiempoPromedioResolucionSeg = agregada.tiempoPromedioResolucionSeg;
        dto.tiempoMinResolucionSeg = agregada.tiempoMinResolucionSeg;
        dto.tiempoMaxResolucionSeg = agregada.tiempoMaxResolucionSeg;

        dto.p50Seg = agregada.percentil50Seg;
        dto.p95Seg = agregada.percentil95Seg;
        dto.p99Seg = agregada.percentil99Seg;

        dto.tiempoPromedioProblemaizacionSeg = agregada.tiempoPromedioProblemaizacionSeg;

        return dto;
    }
}
