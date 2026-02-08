package com.urbanevents.metricas.service;

import com.urbanevents.metricas.domain.EstadoIncidencia;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;

/**
 * Servicio para cálculos de métricas.
 * Contiene lógica pura para transformar timestamps en duraciones,
 * clasificar estados, y calcular tasas.
 * 
 * Totalmente independiente de la capa de datos.
 */
@ApplicationScoped
public class CalculoMetricasService {

    /**
     * Calcula el tiempo transcurrido entre dos instantes en milisegundos.
     */
    public long calcularTiempoEnMs(Instant inicio, Instant fin) {
        if (inicio == null || fin == null) {
            return 0;
        }
        return fin.toEpochMilli() - inicio.toEpochMilli();
    }

    /**
     * Convierte milisegundos a segundos.
     */
    public long msASegundos(long milisegundos) {
        return milisegundos / 1000;
    }

    /**
     * Convierte milisegundos a segundos en punto flotante (double).
     */
    public double msASegundosDouble(long milisegundos) {
        return milisegundos / 1000.0;
    }

    /**
     * Clasifica el estado de una incidencia basado en el nuevoEstado recibido.
     * Mapea estados de evento a nuestros estados internos.
     */
    public EstadoIncidencia clasificarEstado(String nuevoEstadoEvento) {
        if (nuevoEstadoEvento == null) {
            return EstadoIncidencia.PENDIENTE;
        }

        String estado = nuevoEstadoEvento.toUpperCase();

        // Mapeo: los eventos traen estados como "RESUELTO", "CERRADO", "RECHAZADO", etc
        return switch (estado) {
            case "RESUELTO" -> EstadoIncidencia.RESUELTO;
            case "CERRADO" -> EstadoIncidencia.CERRADO;
            case "RECHAZADO" -> EstadoIncidencia.RECHAZADO;
            default -> EstadoIncidencia.PENDIENTE;
        };
    }

    /**
     * Determina si el estado es "resuelto" (final, no pendiente).
     */
    public boolean esResuelto(EstadoIncidencia estado) {
        return estado == EstadoIncidencia.RESUELTO ||
               estado == EstadoIncidencia.CERRADO ||
               estado == EstadoIncidencia.RECHAZADO;
    }

    /**
     * Calcula la tasa de éxito en porcentaje.
     * Éxito = incidencias resueltas / total.
     */
    public double calcularTasaExito(long cantidadResuelta, long cantidadTotal) {
        if (cantidadTotal == 0) {
            return 0.0;
        }
        return (cantidadResuelta * 100.0) / cantidadTotal;
    }

    /**
     * Calcula la tasa de fracaso en porcentaje.
     * Fracaso = incidencias rechazadas / total.
     */
    public double calcularTasaFracaso(long cantidadRechazada, long cantidadTotal) {
        if (cantidadTotal == 0) {
            return 0.0;
        }
        return (cantidadRechazada * 100.0) / cantidadTotal;
    }

    /**
     * Calcula la tasa pendiente en porcentaje.
     */
    public double calcularTasaPendiente(long cantidadPendiente, long cantidadTotal) {
        if (cantidadTotal == 0) {
            return 0.0;
        }
        return (cantidadPendiente * 100.0) / cantidadTotal;
    }

    /**
     * Calcula un promedio seguro (evita división por cero).
     */
    public double calcularPromedio(long suma, long cantidad) {
        if (cantidad == 0) {
            return 0.0;
        }
        return suma / (double) cantidad;
    }
}
