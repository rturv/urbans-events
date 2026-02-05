package com.urbanevents.registro.listeners;

import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.registro.domain.Incidencia;
import com.urbanevents.registro.domain.IncidenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Consumidor de eventos de incidencias priorizadas usando Spring Cloud Stream.
 * Procesa los eventos de incidencias priorizadas y actualiza el estado en la base de datos.
 */
@Component
public class IncidenciaPriorizadaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(IncidenciaPriorizadaConsumer.class);
    
    private final IncidenciaRepository repository;

    public IncidenciaPriorizadaConsumer(IncidenciaRepository repository) {
        this.repository = repository;
    }

    /**
     * Función que consume eventos de incidencias priorizadas.
     * Spring Cloud Stream automáticamente conecta esta función a Kafka mediante la configuración.
     */
    @Bean
    public Consumer<IncidenciaPriorizadaEvent> consumirIncidenciaPriorizada() {
        return event -> {
            logger.info("Recibido evento de incidencia priorizada: {}", event.incidenciaId());
            
            @SuppressWarnings("null")
            Optional<Incidencia> incidenciaOpt = repository.findById(event.incidenciaId());
            
            if (incidenciaOpt.isPresent()) {
                Incidencia incidencia = incidenciaOpt.get();
                incidencia.setPrioridad(event.prioridad());
                repository.save(incidencia);
                logger.info("Incidencia {} actualizada con prioridad: {}", event.incidenciaId(), event.prioridad());
            } else {
                logger.warn("Incidencia con id {} no encontrada en la base de datos", event.incidenciaId());
            }
        };
    }
}
