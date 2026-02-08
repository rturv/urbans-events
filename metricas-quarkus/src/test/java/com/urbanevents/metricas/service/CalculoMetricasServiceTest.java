package com.urbanevents.metricas.service;

import com.urbanevents.metricas.domain.EstadoIncidencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para CalculoMetricasService.
 * Pruebas de lógica pura sin dependencias de BD.
 */
public class CalculoMetricasServiceTest {

    private CalculoMetricasService calculoService;

    @BeforeEach
    public void setUp() {
        calculoService = new CalculoMetricasService();
    }

    @Test
    public void testCalcularTiempoEnMs() {
        // Given
        long ms1 = 1000;
        long ms2 = 5000;
        
        // When
        long resultado = ms2 - ms1;
        
        // Then
        assertEquals(4000, resultado);
    }

    @Test
    public void testMsASegundos() {
        // Given
        long milisegundos = 5000;
        
        // When
        long segundos = calculoService.msASegundos(milisegundos);
        
        // Then
        assertEquals(5, segundos);
    }

    @Test
    public void testMsASegundosDouble() {
        // Given
        long milisegundos = 5500;
        
        // When
        double segundos = calculoService.msASegundosDouble(milisegundos);
        
        // Then
        assertEquals(5.5, segundos);
    }

    @Test
    public void testClasificarEstadoResuelto() {
        // When
        EstadoIncidencia resultado = calculoService.clasificarEstado("RESUELTO");
        
        // Then
        assertEquals(EstadoIncidencia.RESUELTO, resultado);
    }

    @Test
    public void testClasificarEstadoCerrado() {
        // When
        EstadoIncidencia resultado = calculoService.clasificarEstado("CERRADO");
        
        // Then
        assertEquals(EstadoIncidencia.CERRADO, resultado);
    }

    @Test
    public void testClasificarEstadoRechazado() {
        // When
        EstadoIncidencia resultado = calculoService.clasificarEstado("RECHAZADO");
        
        // Then
        assertEquals(EstadoIncidencia.RECHAZADO, resultado);
    }

    @Test
    public void testClasificarEstadoDesconocido() {
        // When
        EstadoIncidencia resultado = calculoService.clasificarEstado("DESCONOCIDO");
        
        // Then
        assertEquals(EstadoIncidencia.PENDIENTE, resultado);
    }

    @Test
    public void testEsResueltoTrue() {
        // When
        boolean resultado = calculoService.esResuelto(EstadoIncidencia.RESUELTO);
        
        // Then
        assertTrue(resultado);
    }

    @Test
    public void testEsResueltoFalse() {
        // When
        boolean resultado = calculoService.esResuelto(EstadoIncidencia.PENDIENTE);
        
        // Then
        assertFalse(resultado);
    }

    @Test
    public void testCalcularTasaExito() {
        // Given
        long resuelta = 80;
        long total = 100;
        
        // When
        double resultado = calculoService.calcularTasaExito(resuelta, total);
        
        // Then
        assertEquals(80.0, resultado);
    }

    @Test
    public void testCalcularTasaExitoConCero() {
        // Given
        long resuelta = 0;
        long total = 0;
        
        // When
        double resultado = calculoService.calcularTasaExito(resuelta, total);
        
        // Then
        assertEquals(0.0, resultado);
    }

    @Test
    public void testCalcularTasaFracaso() {
        // Given
        long rechazada = 20;
        long total = 100;
        
        // When
        double resultado = calculoService.calcularTasaFracaso(rechazada, total);
        
        // Then
        assertEquals(20.0, resultado);
    }

    @Test
    public void testCalcularTasaPendiente() {
        // Given
        long pendiente = 10;
        long total = 100;
        
        // When
        double resultado = calculoService.calcularTasaPendiente(pendiente, total);
        
        // Then
        assertEquals(10.0, resultado);
    }

    @Test
    public void testCalcularPromedio() {
        // Given
        long suma = 1000;
        long cantidad = 10;
        
        // When
        double resultado = calculoService.calcularPromedio(suma, cantidad);
        
        // Then
        assertEquals(100.0, resultado);
    }

    @Test
    public void testCalcularPromedioConCero() {
        // Given
        long suma = 1000;
        long cantidad = 0;
        
        // When
        double resultado = calculoService.calcularPromedio(suma, cantidad);
        
        // Then
        assertEquals(0.0, resultado);
    }
}
