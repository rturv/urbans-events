package com.urbanevents.metricas.scheduler;

import com.urbanevents.metricas.domain.IncidenciaMetricaRepository;
import com.urbanevents.metricas.service.AgregacionMetricasService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.Set;

/**
 * Scheduler que recalcula periódicamente las métricas agregadas.
 * Se ejecuta cada 5 minutos para mantener las estadísticas actualizadas
 * sin impactar el rendimiento del procesamiento de eventos individuales.
 */
@ApplicationScoped
public class AgregacionScheduler {

    private static final Logger LOG = Logger.getLogger(AgregacionScheduler.class);

    @Inject
    AgregacionMetricasService agregacionMetricasService;

    @Inject
    IncidenciaMetricaRepository incidenciaMetricaRepository;

    /**
     * Recalcula todas las métricas agregadas cada 5 minutos.
     * Obtiene todos los pares únicos de tipo/prioridad y recalcula sus estadísticas.
     */
    @Scheduled(every = "5m")
    @Transactional
    public void recalcularTodasLasAgregaciones() {
        LOG.info("Iniciando recálculo de métricas agregadas...");
        
        try {
            Set<IncidenciaMetricaRepository.TipoPrioridadPar> pares = incidenciaMetricaRepository.findDistinctTipoAndPrioridad();
            
            if (pares.isEmpty()) {
                LOG.info("No hay pares tipo/prioridad para recalcular");
                return;
            }

            LOG.infof("Recalculando %d pares tipo/prioridad", pares.size());

            // Recalcular cada par
            for (IncidenciaMetricaRepository.TipoPrioridadPar par : pares) {
                try {
                    agregacionMetricasService.recalcularPorTipoYPrioridad(par.tipo(), par.prioridad());
                } catch (Exception e) {
                    LOG.errorf(e, "Error recalculando tipo=%s, prioridad=%s", par.tipo(), par.prioridad());
                    // Continuar con otros aunque falle uno
                }
            }

            LOG.info("Recálculo de métricas agregadas completado exitosamente");
            
        } catch (Exception e) {
            LOG.errorf(e, "Error durante el recálculo de métricas agregadas");
        }
    }
}
