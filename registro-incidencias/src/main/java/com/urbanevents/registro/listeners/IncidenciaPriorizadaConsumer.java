package com.urbanevents.registro.listeners;

import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.events.Topics;
import com.urbanevents.registro.domain.Incidencia;
import com.urbanevents.registro.domain.IncidenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IncidenciaPriorizadaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(IncidenciaPriorizadaConsumer.class);
    private final IncidenciaRepository repository;

    public IncidenciaPriorizadaConsumer(IncidenciaRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = Topics.INCIDENCIAS_PRIORIZADAS, 
                   groupId = "registro-incidencias-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consumir(IncidenciaPriorizadaEvent event) {
        logger.info("Recibido evento de incidencia priorizada: {}", event.incidenciaId());
        
        Optional<Incidencia> incidenciaOpt = repository.findById(event.incidenciaId());
        
        if (incidenciaOpt.isPresent()) {
            Incidencia incidencia = incidenciaOpt.get();
            incidencia.setPrioridad(event.prioridad());
            repository.save(incidencia);
            logger.info("Incidencia {} actualizada con prioridad: {}", event.incidenciaId(), event.prioridad());
        } else {
            logger.warn("Incidencia con id {} no encontrada en la base de datos", event.incidenciaId());
        }
    }
}
