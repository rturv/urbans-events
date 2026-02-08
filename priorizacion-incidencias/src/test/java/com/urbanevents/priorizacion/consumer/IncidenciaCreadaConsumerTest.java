package com.urbanevents.priorizacion.consumer;

import com.urbanevents.events.EventMetadata;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.priorizacion.domain.Prioridad;
import com.urbanevents.priorizacion.domain.PrioridadIncidencia;
import com.urbanevents.priorizacion.domain.PrioridadRepository;
import com.urbanevents.priorizacion.service.PriorizacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test del procesador funcional de incidencias creadas.
 * Con Spring Cloud Stream el testing es más simple: solo probamos la función pura.
 */
@ExtendWith(MockitoExtension.class)
class IncidenciaCreadaConsumerTest {
    @Mock
    private PriorizacionService priorizacionService;

    @Mock
    private PrioridadRepository repository;

    private IncidenciaCreadaConsumer consumer;
    private Function<IncidenciaCreadaEvent, IncidenciaPriorizadaEvent> procesarIncidencia;

    @BeforeEach
    void setUp() {
        consumer = new IncidenciaCreadaConsumer(priorizacionService, repository);
        procesarIncidencia = consumer.procesarIncidencia();
    }

    @Test
    void procesaIncidenciaConPrioridadAltaYGeneraEventoDeSalida() {
        // Arrange
        when(priorizacionService.calcularPrioridad(anyString())).thenReturn(Prioridad.ALTA);
        when(priorizacionService.calcularMotivo(anyString())).thenReturn("se ha encontrado literal 'incendio' en el campo descripcion");
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IncidenciaCreadaEvent inputEvent = new IncidenciaCreadaEvent(
                new EventMetadata(UUID.randomUUID().toString(), "IncidenciaCreada", Instant.now(), "test", "v1"),
                1L,
                "servicio",
                "descripcion con incendio",
                "sensor",
                "plaza",
                Instant.now()
        );

        // Act
        IncidenciaPriorizadaEvent outputEvent = procesarIncidencia.apply(inputEvent);

        // Assert - Verificar que se persistió la prioridad
        ArgumentCaptor<PrioridadIncidencia> captor = ArgumentCaptor.forClass(PrioridadIncidencia.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPrioridad()).isEqualTo(Prioridad.ALTA);
        assertThat(captor.getValue().getIncidenciaId()).isEqualTo(1L);

        // Assert - Verificar el evento de salida
        assertThat(outputEvent).isNotNull();
        assertThat(outputEvent.incidenciaId()).isEqualTo(1L);
        assertThat(outputEvent.prioridad()).isEqualTo("ALTA");
        assertThat(outputEvent.motivo()).isEqualTo("se ha encontrado literal 'incendio' en el campo descripcion");
        assertThat(outputEvent.metadata().eventType()).isEqualTo("IncidenciaPriorizada");
        assertThat(outputEvent.metadata().sourceService()).isEqualTo("priorizacion-incidencias");
    }

    @Test
    void procesaIncidenciaConPrioridadCriticaYGeneraEventoDeSalida() {
        // Arrange
        when(priorizacionService.calcularPrioridad(anyString())).thenReturn(Prioridad.CRITICA);
        when(priorizacionService.calcularMotivo(anyString())).thenReturn("se ha encontrado palabra crítica 'accidente' o 'grave' en el campo descripcion");
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IncidenciaCreadaEvent inputEvent = new IncidenciaCreadaEvent(
                new EventMetadata(UUID.randomUUID().toString(), "IncidenciaCreada", Instant.now(), "test", "v1"),
                3L,
                "seguridad",
                "accidente grave en la plaza",
                "sensor",
                "plaza central",
                Instant.now(),""
        );

        // Act
        IncidenciaPriorizadaEvent outputEvent = procesarIncidencia.apply(inputEvent);

        // Assert - Verificar que se persistió la prioridad
        ArgumentCaptor<PrioridadIncidencia> captor = ArgumentCaptor.forClass(PrioridadIncidencia.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPrioridad()).isEqualTo(Prioridad.CRITICA);
        assertThat(captor.getValue().getIncidenciaId()).isEqualTo(3L);

        // Assert - Verificar el evento de salida
        assertThat(outputEvent).isNotNull();
        assertThat(outputEvent.incidenciaId()).isEqualTo(3L);
        assertThat(outputEvent.prioridad()).isEqualTo("CRITICA");
        assertThat(outputEvent.motivo()).isEqualTo("se ha encontrado palabra crítica 'accidente' o 'grave' en el campo descripcion");
        assertThat(outputEvent.metadata().eventType()).isEqualTo("IncidenciaPriorizada");
        assertThat(outputEvent.metadata().sourceService()).isEqualTo("priorizacion-incidencias");
    }

    @Test
    void procesaIncidenciaConPrioridadMediaCuandoNoHayPalabrasCriticas() {
        // Arrange
        when(priorizacionService.calcularPrioridad(anyString())).thenReturn(Prioridad.MEDIA);
        when(priorizacionService.calcularMotivo(anyString())).thenReturn("prioridad por defecto");
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IncidenciaCreadaEvent inputEvent = new IncidenciaCreadaEvent(
                new EventMetadata(UUID.randomUUID().toString(), "IncidenciaCreada", Instant.now(), "test", "v1"),
                2L,
                "alumbrado",
                "farola apagada",
                "ciudadano",
                "calle mayor",
                Instant.now(),""
        );

        // Act
        IncidenciaPriorizadaEvent outputEvent = procesarIncidencia.apply(inputEvent);

        // Assert
        ArgumentCaptor<PrioridadIncidencia> captor = ArgumentCaptor.forClass(PrioridadIncidencia.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPrioridad()).isEqualTo(Prioridad.MEDIA);

        assertThat(outputEvent.prioridad()).isEqualTo("MEDIA");
        assertThat(outputEvent.motivo()).isEqualTo("prioridad por defecto");
    }
}