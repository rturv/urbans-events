package com.urbanevents.priorizacion.consumer;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.events.Topics;
import com.urbanevents.priorizacion.domain.PrioridadIncidencia;
import com.urbanevents.priorizacion.domain.PrioridadRepository;
import com.urbanevents.priorizacion.service.PriorizacionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class IncidenciaCreadaConsumer {
    private final PriorizacionService priorizacionService;
    private final PrioridadRepository repository;
    private final KafkaTemplate<String, IncidenciaPriorizadaEvent> kafkaTemplate;

    public IncidenciaCreadaConsumer(PriorizacionService priorizacionService,
                                    PrioridadRepository repository,
                                    KafkaTemplate<String, IncidenciaPriorizadaEvent> kafkaTemplate) {
        this.priorizacionService = priorizacionService;
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = Topics.INCIDENCIAS_CREADAS, groupId = "priorizacion-service")
    public void onMessage(IncidenciaCreadaEvent event) throws Exception {
        String prioridad = priorizacionService.calcularPrioridad(event.descripcion());
        String motivo = priorizacionService.calcularMotivo(event.descripcion());
        Instant now = Instant.now();

        repository.save(new PrioridadIncidencia(event.incidenciaId(), prioridad, motivo, now));

        EventMetadata metadata = new EventMetadata(UUID.randomUUID().toString(), "IncidenciaPriorizada",
                now, "priorizacion-incidencias", "v1");
        IncidenciaPriorizadaEvent priorizadaEvent = new IncidenciaPriorizadaEvent(
            metadata, event.incidenciaId(), prioridad, motivo, now);
        
        // Espera a que el mensaje se envíe correctamente
        try {
            kafkaTemplate.send(Topics.INCIDENCIAS_PRIORIZADAS, String.valueOf(event.incidenciaId()), priorizadaEvent).get();
        } catch (Exception e) {
            System.err.println("Error enviando evento priorizacion a Kafka: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error enviando evento a Kafka", e);
        }
    }
}
