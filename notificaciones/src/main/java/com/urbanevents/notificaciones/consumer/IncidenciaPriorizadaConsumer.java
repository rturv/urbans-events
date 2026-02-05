package com.urbanevents.notificaciones.consumer;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaNotificadaEvent;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.events.Topics;
import com.urbanevents.notificaciones.domain.Notificacion;
import com.urbanevents.notificaciones.domain.NotificacionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class IncidenciaPriorizadaConsumer {
    private final NotificacionRepository repository;
    private final KafkaTemplate<String, IncidenciaNotificadaEvent> kafkaTemplate;
    private final String destinatarios;

    public IncidenciaPriorizadaConsumer(NotificacionRepository repository,
                                        KafkaTemplate<String, IncidenciaNotificadaEvent> kafkaTemplate,
                                        @Value("${notificaciones.destinatarios}") String destinatarios) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.destinatarios = destinatarios;
    }

    @KafkaListener(topics = Topics.INCIDENCIAS_PRIORIZADAS, groupId = "notificaciones-service")
    public void onMessage(IncidenciaPriorizadaEvent event) throws Exception {
        if (!"alta".equalsIgnoreCase(event.prioridad())) {
            return;
        }

        Long id = null;
        Instant now = Instant.now();
        String canal = "email";
        String estado = "enviada";

        repository.save(new Notificacion(id, event.incidenciaId(), canal, destinatarios, estado, now));

        EventMetadata metadata = new EventMetadata(UUID.randomUUID().toString(), "IncidenciaNotificada",
                now, "notificaciones", "v1");
        IncidenciaNotificadaEvent notificadaEvent = new IncidenciaNotificadaEvent(metadata, event.incidenciaId(),
            canal, destinatarios, estado, now);
        
        // Espera a que el mensaje se envíe correctamente
        try {
            kafkaTemplate.send(Topics.INCIDENCIAS_NOTIFICADAS, String.valueOf(event.incidenciaId()), notificadaEvent).get();
        } catch (Exception e) {
            System.err.println("Error enviando evento notificacion a Kafka: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error enviando evento a Kafka", e);
        }
    }
}
