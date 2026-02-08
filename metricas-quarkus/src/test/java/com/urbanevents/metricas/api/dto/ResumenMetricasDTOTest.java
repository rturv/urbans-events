package com.urbanevents.metricas.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para los DTOs.
 * Validación de creación y conversión de datos.
 */
public class ResumenMetricasDTOTest {

    @Test
    public void testResumenMetricasDTOCreacion() {
        // Given & When
        ResumenMetricasDTO dto = new ResumenMetricasDTO(100L, 80L, 15L, 5L);

        // Then
        assertEquals(100L, dto.totalIncidencias);
        assertEquals(80L, dto.incidenciasResueltas);
        assertEquals(15L, dto.incidenciasPendientes);
        assertEquals(5L, dto.incidenciasRechazadas);
    }

    @Test
    public void testResumenMetricasDTOConTasas() {
        // Given
        ResumenMetricasDTO dto = new ResumenMetricasDTO(100L, 80L, 15L, 5L);
        dto.tasaExitoPct = 80.0;
        dto.tasaPendientePct = 15.0;
        dto.tasaFracasoPct = 5.0;

        // When & Then
        assertEquals(80.0, dto.tasaExitoPct);
        assertEquals(15.0, dto.tasaPendientePct);
        assertEquals(5.0, dto.tasaFracasoPct);
    }

    @Test
    public void testMetricaAgregadaDTOCreacion() {
        // Given & When
        MetricaAgregadaDTO dto = new MetricaAgregadaDTO("INCENDIO", "ALTA", 50L);

        // Then
        assertEquals("INCENDIO", dto.tipoIncidencia);
        assertEquals("ALTA", dto.prioridad);
        assertEquals(50L, dto.cantidadTotal);
    }

    @Test
    public void testMetricaIncidenciaDTOCreacion() {
        // Given & When
        MetricaIncidenciaDTO dto = new MetricaIncidenciaDTO(123L, "ROBO", "MEDIA", "PENDIENTE");

        // Then
        assertEquals(123L, dto.incidenciaId);
        assertEquals("ROBO", dto.tipo);
        assertEquals("MEDIA", dto.prioridad);
        assertEquals("PENDIENTE", dto.estado);
    }

    @Test
    public void testEstadisticasTipoDTOCreacion() {
        // Given & When
        EstadisticasTipoDTO dto = new EstadisticasTipoDTO("INCENDIO", 100L);

        // Then
        assertEquals("INCENDIO", dto.tipo);
        assertEquals(100L, dto.cantidad);
    }

    @Test
    public void testIncidenciaPendienteDTOCreacion() {
        // Given & When
        IncidenciaPendienteDTO dto = new IncidenciaPendienteDTO(456L, "INUNDACION", "BAJA", "PENDIENTE");

        // Then
        assertEquals(456L, dto.incidenciaId);
        assertEquals("INUNDACION", dto.tipo);
        assertEquals("BAJA", dto.prioridad);
        assertEquals("PENDIENTE", dto.estado);
    }

    @Test
    public void testIncidenciaPendienteDTOConTiempos() {
        // Given
        IncidenciaPendienteDTO dto = new IncidenciaPendienteDTO(456L, "INUNDACION", "BAJA", "PENDIENTE");
        dto.tiempoDesdeCreacionSeg = 3600L;
        dto.tiempoDesdeCreacionMin = 60L;

        // When & Then
        assertEquals(3600L, dto.tiempoDesdeCreacionSeg);
        assertEquals(60L, dto.tiempoDesdeCreacionMin);
    }
}
