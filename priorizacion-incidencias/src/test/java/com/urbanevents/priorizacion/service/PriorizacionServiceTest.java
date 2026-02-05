package com.urbanevents.priorizacion.service;

import com.urbanevents.priorizacion.domain.Prioridad;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PriorizacionServiceTest {
    private final PriorizacionService service = new PriorizacionService(List.of("fuego", "urgente"));

    @Test
    void devuelveCriticaCuandoDescripcionContieneAccidente() {
        assertThat(service.calcularPrioridad("Ha habido un accidente grave")).isEqualTo(Prioridad.CRITICA);
    }

    @Test
    void devuelveCriticaCuandoDescripcionContieneGrave() {
        assertThat(service.calcularPrioridad("Situacion muy grave")).isEqualTo(Prioridad.CRITICA);
    }

    @Test
    void devuelveAltaCuandoDescripcionContienePalabrasCriticas() {
        assertThat(service.calcularPrioridad("Hay un fuego urgente")).isEqualTo(Prioridad.ALTA);
    }

    @Test
    void devuelveMediaCuandoDescripcionEsNull() {
        assertThat(service.calcularPrioridad(null)).isEqualTo(Prioridad.MEDIA);
    }
}