package com.urbanevents.metricas.consumer;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.metricas.domain.Indicador;
import com.urbanevents.metricas.domain.IndicadorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IncidenciaCreadaConsumerTest {
    @Mock
    private IndicadorRepository repository;

    @InjectMocks
    private IncidenciaCreadaConsumer consumer;

    @Test
    void incrementaIndicadorExistente() throws Exception {
        Indicador indicador = new Indicador(1L, "Alerta");
        when(repository.findByTipo("Alerta")).thenReturn(Optional.of(indicador));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        consumer.onMessage(crearEvento());

        ArgumentCaptor<Indicador> captor = ArgumentCaptor.forClass(Indicador.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getTotal()).isEqualTo(1L);
        assertThat(captor.getValue().getTipo()).isEqualTo("Alerta");
    }

    @Test
    void creaIndicadorCuandoNoExiste() throws Exception {
        when(repository.findByTipo("Alerta")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        consumer.onMessage(crearEvento());

        ArgumentCaptor<Indicador> captor = ArgumentCaptor.forClass(Indicador.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getTotal()).isEqualTo(1L);
    }

    private IncidenciaCreadaEvent crearEvento() {
        EventMetadata metadata = new EventMetadata(UUID.randomUUID().toString(), "IncidenciaCreada",
                Instant.now(), "metricas-test", "v1");
        return new IncidenciaCreadaEvent(metadata, 1L, "Alerta", "desc", "origen", "ubicacion", Instant.now());
    }
}