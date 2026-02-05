package com.urbanevents.priorizacion.consumer;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.priorizacion.domain.Prioridad;
import com.urbanevents.priorizacion.domain.PrioridadIncidencia;
import com.urbanevents.priorizacion.domain.PrioridadRepository;
import com.urbanevents.priorizacion.service.PriorizacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

/**
 * Procesador funcional de eventos de incidencias creadas usando Spring Cloud Stream.
 * Consume eventos de incidencias creadas y produce eventos de incidencias priorizadas.
 */
@Component
public class IncidenciaCreadaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(IncidenciaCreadaConsumer.class);
    
    private final PriorizacionService priorizacionService;
    private final PrioridadRepository repository;

    public IncidenciaCreadaConsumer(PriorizacionService priorizacionService,
                                    PrioridadRepository repository) {
        this.priorizacionService = priorizacionService;
        this.repository = repository;
    }

    /**
     * Función que procesa eventos de incidencias creadas y genera eventos priorizados.
     * Spring Cloud Stream automáticamente conecta esta función a Kafka mediante la configuración.
     */
    @Bean
    public Function<IncidenciaCreadaEvent, IncidenciaPriorizadaEvent> procesarIncidencia() {
        return event -> {
            logger.info("Procesando incidencia creada: {}", event.incidenciaId());
            
            // Calcular prioridad y motivo
            Prioridad prioridad = priorizacionService.calcularPrioridad(event.descripcion());
            String motivo = priorizacionService.calcularMotivo(event.descripcion());
            Instant now = Instant.now();

            // Persistir la prioridad calculada
            repository.save(new PrioridadIncidencia(event.incidenciaId(), prioridad, motivo, now));
            logger.info("Incidencia {} priorizada como: {} (motivo: {})", 
                       event.incidenciaId(), prioridad, motivo);

            // Crear evento de salida
            EventMetadata metadata = new EventMetadata(
                UUID.randomUUID().toString(), 
                "IncidenciaPriorizada",
                now, 
                "priorizacion-incidencias", 
                "v1"
            );
            
            IncidenciaPriorizadaEvent priorizadaEvent = new IncidenciaPriorizadaEvent(
                metadata, 
                event.incidenciaId(), 
                prioridad.name(), 
                motivo, 
                now
            );
            
            logger.info("Publicando evento de incidencia priorizada: {}", event.incidenciaId());
            return priorizadaEvent;
        };
    }
}
