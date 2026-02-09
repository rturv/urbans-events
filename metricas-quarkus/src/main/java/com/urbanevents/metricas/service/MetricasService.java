package com.urbanevents.metricas.service;

import com.urbanevents.events.*;
import com.urbanevents.metricas.domain.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.jboss.logging.Logger;

/**
 * Servicio principal que orquesta el procesamiento de eventos de Kafka.
 * Cada método recibe un evento específico y actualiza las métricas.
 */
@ApplicationScoped
public class MetricasService {

    private static final Logger LOG = Logger.getLogger(MetricasService.class);

    @Inject
    IncidenciaMetricaRepository incidenciaMetricaRepository;

    @Inject
    CalculoMetricasService calculoMetricasService;

    @Inject
    AgregacionMetricasService agregacionMetricasService;

    /**
     * Procesa un evento de incidencia creada.
     * Crea un nuevo registro en INCIDENCIAS_METRICAS con el timestamp de creación.
     */
    @Transactional
    public void procesarCreacion(IncidenciaCreadaEvent evento) {
        try {
            LOG.infof("Procesando IncidenciaCreadaEvent para incidencia %d, tipo: %s",
                    evento.incidenciaId(), evento.tipo());

            // Verificar si ya existe
            IncidenciaMetrica metrica = incidenciaMetricaRepository.findByIncidenciaId(evento.incidenciaId());
            
            if (metrica != null) {
                LOG.warnf("Incidencia %d ya existe, ignorando evento de creación", evento.incidenciaId());
                return;
            }

            // Crear nuevo registro
            metrica = new IncidenciaMetrica(
                    evento.incidenciaId(),
                    evento.tipo(),
                    evento.creadaEn()
            );

            metrica.estadoActual = EstadoIncidencia.PENDIENTE;
            metrica.ultimaActualizacion = Instant.now();

            // Guardar
            incidenciaMetricaRepository.persist(metrica);
            LOG.infof("Incidencia %d creada exitosamente", evento.incidenciaId());

        } catch (Exception e) {
            LOG.errorf("Error procesando IncidenciaCreadaEvent: %s", e.getMessage());
            throw e;
        }
    }

    /**
     * Procesa un evento de incidencia priorizada.
     * Actualiza el registro con el timestamp de priorización y calcula tiempos.
     */
    @Transactional
    public void procesarPriorizacion(IncidenciaPriorizadaEvent evento) {
        try {
            LOG.infof("Procesando IncidenciaPriorizadaEvent para incidencia %d, prioridad: %s",
                    evento.incidenciaId(), evento.prioridad());

            IncidenciaMetrica metrica = incidenciaMetricaRepository.findByIncidenciaId(evento.incidenciaId());

            if (metrica == null) {
                LOG.warnf("Incidencia %d no encontrada, creando registro base", evento.incidenciaId());
                metrica = new IncidenciaMetrica(
                        evento.incidenciaId(),
                        "DESCONOCIDO", // No tenemos tipo
                        evento.priorizadaEn()
                );
            }

            // Actualizar información
            metrica.prioridad = evento.prioridad();
            metrica.tiempoPriorizacion = evento.priorizadaEn();

            // Calcular tiempo de priorización (desde creación)
            if (metrica.tiempoCreacion != null) {
                long msPriorizacion = calculoMetricasService.calcularTiempoEnMs(
                        metrica.tiempoCreacion,
                        evento.priorizadaEn()
                );
                metrica.msPriorizacion = msPriorizacion;
            }

            metrica.ultimaActualizacion = Instant.now();

            // Guardar
            incidenciaMetricaRepository.persist(metrica);

            LOG.infof("Incidencia %d priorizada exitosamente", evento.incidenciaId());

        } catch (Exception e) {
            LOG.errorf("Error procesando IncidenciaPriorizadaEvent: %s", e.getMessage());
            throw e;
        }
    }

    /**
     * Procesa un evento de incidencia notificada.
     * Actualiza el timestamp de notificación.
     */
    @Transactional
    public void procesarNotificacion(IncidenciaNotificadaEvent evento) {
        try {
            LOG.infof("Procesando IncidenciaNotificadaEvent para incidencia %d, canal: %s",
                    evento.incidenciaId(), evento.canal());

            IncidenciaMetrica metrica = incidenciaMetricaRepository.findByIncidenciaId(evento.incidenciaId());

            if (metrica == null) {
                LOG.warnf("Incidencia %d no encontrada en evento notificación", evento.incidenciaId());
                return;
            }

            // Actualizar información
            metrica.tiempoNotificacion = evento.notificadaEn();

            // Calcular tiempo de notificación (desde priorización, si existe)
            if (metrica.tiempoPriorizacion != null) {
                long msNotificacion = calculoMetricasService.calcularTiempoEnMs(
                        metrica.tiempoPriorizacion,
                        evento.notificadaEn()
                );
                metrica.msNotificacion = msNotificacion;
            }

            metrica.ultimaActualizacion = Instant.now();

            // Guardar
            incidenciaMetricaRepository.persist(metrica);

            LOG.infof("Incidencia %d notificada exitosamente", evento.incidenciaId());

        } catch (Exception e) {
            LOG.errorf("Error procesando IncidenciaNotificadaEvent: %s", e.getMessage());
            throw e;
        }
    }

    /**
     * Procesa un evento de cambio de incidencia (cambio de estado).
     * Este es el evento más importante que marca resolución/cierre/rechazo.
     */
    @Transactional
    public void procesarCambio(IncidenciaChangedEvent evento) {
        try {
            LOG.infof("Procesando IncidenciaChangedEvent para incidencia %d, nuevo estado: %s",
                    evento.incidenciaId(), evento.nuevoEstado());

            IncidenciaMetrica metrica = incidenciaMetricaRepository.findByIncidenciaId(evento.incidenciaId());

            if (metrica == null) {
                LOG.warnf("Incidencia %d no encontrada en evento cambio", evento.incidenciaId());
                return;
            }

            // Clasificar y actualizar estado
            EstadoIncidencia nuevoEstado = calculoMetricasService.clasificarEstado(evento.nuevoEstado());
            metrica.estadoActual = nuevoEstado;
            metrica.esResuelto = calculoMetricasService.esResuelto(nuevoEstado);

            // Actualizar timestamp de resolución solo si es resuelto
            if (metrica.esResuelto) {
                metrica.tiempoResolucion = evento.cambiadoEn();

                // Calcular tiempo de resolución (desde creación)
                if (metrica.tiempoCreacion != null) {
                    long msResolucion = calculoMetricasService.calcularTiempoEnMs(
                            metrica.tiempoCreacion,
                            evento.cambiadoEn()
                    );
                    metrica.msResolucion = msResolucion;
                }
            }

            metrica.ultimaActualizacion = Instant.now();

            // Guardar
            incidenciaMetricaRepository.persist(metrica);

            LOG.infof("Incidencia %d cambiada a estado %s exitosamente", 
                    evento.incidenciaId(), nuevoEstado);

        } catch (Exception e) {
            LOG.errorf("Error procesando IncidenciaChangedEvent: %s", e.getMessage());
            throw e;
        }
    }
}
