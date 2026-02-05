package com.urbanevents.notificaciones.consumer;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaNotificadaEvent;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.events.Topics;
import com.urbanevents.notificaciones.domain.Notificacion;
import com.urbanevents.notificaciones.domain.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidenciaPriorizadaConsumerTest {
    @Mock
    private NotificacionRepository repository;

    @Mock
    private KafkaTemplate<String, IncidenciaNotificadaEvent> kafkaTemplate;

    private IncidenciaPriorizadaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new IncidenciaPriorizadaConsumer(repository, kafkaTemplate, "equipo@urban-events.org");
    }

    @Test
    void ignoraPrioridadesMedias() throws Exception {
        IncidenciaPriorizadaEvent event = crearEvento("media");

        consumer.onMessage(event);

        verifyNoInteractions(repository, kafkaTemplate);
    }

    @Test
    void persisteYPublicaParaPrioridadAlta() throws Exception {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock del futuro para que send() funcione correctamente
        java.util.concurrent.CompletableFuture<org.springframework.kafka.support.SendResult<String, IncidenciaNotificadaEvent>> future = 
            new java.util.concurrent.CompletableFuture<>();
        future.complete(null);
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        IncidenciaPriorizadaEvent event = crearEvento("alta");

        consumer.onMessage(event);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getIncidenciaId()).isEqualTo(event.incidenciaId());
        assertThat(captor.getValue().getEstado()).isEqualTo("enviada");

        verify(kafkaTemplate).send(eq(Topics.INCIDENCIAS_NOTIFICADAS), eq(String.valueOf(event.incidenciaId())), any(IncidenciaNotificadaEvent.class));
    }

    private IncidenciaPriorizadaEvent crearEvento(String prioridad) {
        EventMetadata metadata = new EventMetadata(UUID.randomUUID().toString(), "IncidenciaPriorizada",
                Instant.now(), "notificaciones-test", "v1");
        return new IncidenciaPriorizadaEvent(metadata, 1L, prioridad, "motivo", Instant.now());
    }
}