package com.urbanevents.registro.api;

import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.events.IncidenciaChangedEvent;
import com.urbanevents.registro.domain.Incidencia;
import com.urbanevents.registro.domain.IncidenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertThat(captor.getValue().getEstado()).isEqualTo("REGISTRADA");

        verify(streamBridge).send(eq("incidenciasCreadas-out-0"), any(IncidenciaCreadaEvent.class));
    }

    @Test
    void cambiarIncidenciaActualizaEstadoYComentario() throws Exception {
        Incidencia incidenciaExistente = new Incidencia(1L, "fuego", "alerta de fuego", "sensor", "Centro", "registrada", Instant.now());
        CambiarIncidenciaRequest request = new CambiarIncidenciaRequest("EN_PROGRESO", "Se ha asignado al equipo");

        when(repository.findById(1L)).thenReturn(Optional.of(incidenciaExistente));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(streamBridge.send(any(), any())).thenReturn(true);

        IncidenciaChangedEvent event = controller.cambiarIncidencia(1L, request);

        assertThat(event.incidenciaId()).isEqualTo(1L);
        assertThat(event.nuevoEstado()).isEqualTo("EN_PROGRESO");
        assertThat(event.comentario()).isEqualTo("Se ha asignado al equipo");

        ArgumentCaptor<Incidencia> captor = ArgumentCaptor.forClass(Incidencia.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo("EN_PROGRESO");
        assertThat(captor.getValue().getComentarios()).contains("Se ha asignado al equipo");

        verify(streamBridge).send(eq("incidenciasChanges-out-0"), any(IncidenciaChangedEvent.class));
    }

    @Test
    void cambiarIncidenciaSinComentario() throws Exception {
        Incidencia incidenciaExistente = new Incidencia(1L, "fuego", "alerta de fuego", "sensor", "Centro", "registrada", Instant.now());
        CambiarIncidenciaRequest request = new CambiarIncidenciaRequest("CERRADA", null);

        when(repository.findById(1L)).thenReturn(Optional.of(incidenciaExistente));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(streamBridge.send(any(), any())).thenReturn(true);

        IncidenciaChangedEvent event = controller.cambiarIncidencia(1L, request);

        assertThat(event.incidenciaId()).isEqualTo(1L);
        assertThat(event.nuevoEstado()).isEqualTo("CERRADA");
        assertThat(event.comentario()).isNull();

        ArgumentCaptor<Incidencia> captor = ArgumentCaptor.forClass(Incidencia.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo("CERRADA");
        assertThat(captor.getValue().getComentarios()).isEmpty();
    }

    @Test
    void cambiarIncidenciaLanzaExcepcionSiNoExiste() {
        CambiarIncidenciaRequest request = new CambiarIncidenciaRequest("EN_PROGRESO", "comentario");

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.cambiarIncidencia(999L, request))
                .isInstanceOf(ResponseStatusException.class);
    }
}