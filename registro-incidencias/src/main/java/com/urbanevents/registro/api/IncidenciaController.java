package com.urbanevents.registro.api;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.registro.domain.Incidencia;
import com.urbanevents.registro.domain.IncidenciaRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
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
    private final StreamBridge streamBridge;

    public IncidenciaController(IncidenciaRepository repository,
                                StreamBridge streamBridge) {
        this.repository = repository;
        this.streamBridge = streamBridge;
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

        // Enviar evento a Kafka usando StreamBridge
        try {
            streamBridge.send("incidenciasCreadas-out-0", event);
        } catch (Exception e) {
            System.err.println("Error enviando mensaje a Kafka: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error enviando evento a Kafka", e);
        }
        return event;
    }
}
