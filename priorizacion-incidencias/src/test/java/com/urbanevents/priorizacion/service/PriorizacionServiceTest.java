package com.urbanevents.priorizacion.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PriorizacionServiceTest {
    private final PriorizacionService service = new PriorizacionService(List.of("fuego", "urgente"));

    @Test
    void devuelveAltaCuandoDescripcionContienePalabrasCriticas() {
        assertThat(service.calcularPrioridad("Hay un fuego urgente")).isEqualTo("alta");
    }

    @Test
    void devuelveMediaCuandoDescripcionEsNull() {
        assertThat(service.calcularPrioridad(null)).isEqualTo("media");
    }
}