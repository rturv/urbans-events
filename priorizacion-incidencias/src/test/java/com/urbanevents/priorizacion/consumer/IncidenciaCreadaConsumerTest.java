package com.urbanevents.priorizacion.consumer;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.events.Topics;
import com.urbanevents.priorizacion.domain.PrioridadIncidencia;
import com.urbanevents.priorizacion.domain.PrioridadRepository;
import com.urbanevents.priorizacion.service.PriorizacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidenciaCreadaConsumerTest {
    @Mock
    private PriorizacionService priorizacionService;

    @Mock
    private PrioridadRepository repository;

    @Mock
    private KafkaTemplate<String, IncidenciaPriorizadaEvent> kafkaTemplate;

    @InjectMocks
    private IncidenciaCreadaConsumer consumer;

    @Test
    void procesaAltaYPublicaEvento() throws Exception {
        when(priorizacionService.calcularPrioridad(anyString())).thenReturn("alta");
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock del futuro para que send() funcione correctamente
        CompletableFuture<SendResult<String, IncidenciaPriorizadaEvent>> future = new CompletableFuture<>();
        future.complete(null);
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        IncidenciaCreadaEvent event = new IncidenciaCreadaEvent(
                new EventMetadata(UUID.randomUUID().toString(), "IncidenciaCreada", Instant.now(), "test", "v1"),
            1L,
                "servicio",
                "descripcion crítica",
                "sensor",
                "plaza",
                Instant.now()
        );

        consumer.onMessage(event);

        ArgumentCaptor<PrioridadIncidencia> captor = ArgumentCaptor.forClass(PrioridadIncidencia.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPrioridad()).isEqualTo("alta");

        verify(kafkaTemplate).send(eq(Topics.INCIDENCIAS_PRIORIZADAS), eq(String.valueOf(event.incidenciaId())), any(IncidenciaPriorizadaEvent.class));
    }
}