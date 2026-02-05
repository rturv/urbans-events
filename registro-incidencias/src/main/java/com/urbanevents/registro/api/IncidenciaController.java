package com.urbanevents.registro.api;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.events.Topics;
import com.urbanevents.registro.domain.Incidencia;
import com.urbanevents.registro.domain.IncidenciaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/incidencias")
public class IncidenciaController {
    private final IncidenciaRepository repository;
    private final KafkaTemplate<String, IncidenciaCreadaEvent> kafkaTemplate;

    public IncidenciaController(IncidenciaRepository repository,
                                KafkaTemplate<String, IncidenciaCreadaEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidenciaCreadaEvent crear(@RequestBody NuevaIncidenciaRequest request) throws Exception {
        Instant now = Instant.now();
        Incidencia incidencia = new Incidencia(null, request.tipo(), request.descripcion(), request.origen(),
                request.ubicacion(), "registrada", now);
        repository.save(incidencia);

        Long generatedId = incidencia.getId();

        EventMetadata metadata = new EventMetadata(UUID.randomUUID().toString(), "IncidenciaCreada",
                now, "registro-incidencias", "v1");
        IncidenciaCreadaEvent event = new IncidenciaCreadaEvent(metadata, generatedId, request.tipo(),
                request.descripcion(), request.origen(), request.ubicacion(), now);

        // Espera a que el mensaje se envíe correctamente
        try {
            kafkaTemplate.send(Topics.INCIDENCIAS_CREADAS, String.valueOf(generatedId), event).get();
        } catch (Exception e) {
            System.err.println("Error enviando mensaje a Kafka: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error enviando evento a Kafka", e);
        }
        return event;
    }
}
