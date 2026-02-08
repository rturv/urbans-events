package com.urbanevents.registro.api;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.events.IncidenciaChangedEvent;
import com.urbanevents.registro.domain.Incidencia;
import com.urbanevents.registro.domain.IncidenciaRepository;

import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

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
    public IncidenciaCreadaEvent crear(@Valid @RequestBody NuevaIncidenciaRequest request) throws Exception {
        Instant now = Instant.now();
        Incidencia incidencia = new Incidencia(null, request.tipo(), request.descripcion(), request.origen(),
                request.ubicacion(), "REGISTRADA", now);
        repository.save(incidencia);

        Long generatedId = incidencia.getId();

        EventMetadata metadata = new EventMetadata(UUID.randomUUID().toString(), "IncidenciaCreada",
                now, "registro-incidencias", "v1",incidencia.getEstado());
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

    @PostMapping("/{id}/cambios")
    @ResponseStatus(HttpStatus.OK)
    public IncidenciaChangedEvent cambiarIncidencia(
            @Parameter(description = "ID de la incidencia", required = true)            
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody CambiarIncidenciaRequest request) throws Exception {
        
        // 1. Validar que la incidencia existe
        Optional<Incidencia> incidenciaOpt = repository.findById(id);
        if (incidenciaOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Incidencia no encontrada");
        }

        Incidencia incidencia = incidenciaOpt.get();
        Instant now = Instant.now();

        // 2. Actualizar estado
        incidencia.setEstado(request.nuevoEstado());

        // 3. Agregar comentario si está presente
        if (request.comentario() != null && !request.comentario().trim().isEmpty()) {
            incidencia.agregarComentario(request.comentario());
        }

        // 4. Guardar en BD
        repository.save(incidencia);

        // 5. Crear evento
        EventMetadata metadata = new EventMetadata(
                UUID.randomUUID().toString(),
                "IncidenciaChanged",
                now,
                "registro-incidencias",
                "v1",incidencia.getEstado()
        );

        IncidenciaChangedEvent event = new IncidenciaChangedEvent(
                metadata,
                id,
                request.nuevoEstado(),
                request.comentario(),
                now
        );

        // 6. Publicar evento a Kafka
        try {
            streamBridge.send("incidenciasChanges-out-0", event);
        } catch (Exception e) {
            System.err.println("Error enviando evento a Kafka: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error enviando evento a Kafka", e);
        }

        return event;
    }
}
