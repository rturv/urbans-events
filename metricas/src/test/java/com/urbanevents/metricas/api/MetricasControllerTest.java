package com.urbanevents.metricas.api;

import com.urbanevents.metricas.domain.Indicador;
import com.urbanevents.metricas.domain.IndicadorRepository;
import com.urbanevents.metricas.domain.ResumenNotificaciones;
import com.urbanevents.metricas.domain.ResumenNotificacionesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricasControllerTest {
    @Mock
    private IndicadorRepository indicadorRepository;

    @Mock
    private ResumenNotificacionesRepository resumenRepository;

    private MetricasController controller;

    @BeforeEach
    void setUp() {
        controller = new MetricasController(indicadorRepository, resumenRepository);
    }

    @Test
    void listarAgregaIndicadoresYResumenDeNotificaciones() {
        Indicador tipo = new Indicador(1L, "Alerta");
        tipo.incrementarTotal();
        Indicador prioridades = new Indicador(2L, "prioridades");
        prioridades.incrementarPrioridad("alta");
        prioridades.incrementarPrioridad("media");
        ResumenNotificaciones resumen = new ResumenNotificaciones(1L);
        resumen.incrementar();

        when(indicadorRepository.findAll()).thenReturn(List.of(tipo, prioridades));
        when(resumenRepository.findAll()).thenReturn(List.of(resumen));

        ResumenResponse response = controller.listar();

        assertThat(response.incidenciasPorTipo()).containsEntry("Alerta", 1L);
        assertThat(response.prioridades()).containsEntry("alta", 1L)
                .containsEntry("media", 1L)
                .containsEntry("baja", 0L);
        assertThat(response.notificacionesTotales()).isEqualTo(1L);
    }
}