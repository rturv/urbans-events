package com.urbanevents.registro.api;

import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.registro.domain.Incidencia;
import com.urbanevents.registro.domain.IncidenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IncidenciaControllerTest {
    @Mock
    private IncidenciaRepository repository;

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private IncidenciaController controller;

    @Test
    void crearPersisteYPublicaEvento() throws Exception {
        NuevaIncidenciaRequest request = new NuevaIncidenciaRequest("fuego", "alerta de fuego", "sensor", "Centro");

        when(repository.save(any())).thenAnswer(invocation -> {
            Incidencia i = invocation.getArgument(0);
            i.setId(1L);
            return i;
        });

        // Mock de StreamBridge para que el send funcione correctamente
        when(streamBridge.send(any(), any())).thenReturn(true);

        IncidenciaCreadaEvent event = controller.crear(request);

        assertThat(event.incidenciaId()).isNotNull();
        assertThat(event.incidenciaId()).isPositive();
        assertThat(event.tipo()).isEqualTo("fuego");
        assertThat(event.descripcion()).isEqualTo("alerta de fuego");
        assertThat(event.origen()).isEqualTo("sensor");
        assertThat(event.ubicacion()).isEqualTo("Centro");

        ArgumentCaptor<Incidencia> captor = ArgumentCaptor.forClass(Incidencia.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getTipo()).isEqualTo("fuego");
        assertThat(captor.getValue().getEstado()).isEqualTo("registrada");

        verify(streamBridge).send(eq("incidenciasCreadas-out-0"), any(IncidenciaCreadaEvent.class));
    }
}