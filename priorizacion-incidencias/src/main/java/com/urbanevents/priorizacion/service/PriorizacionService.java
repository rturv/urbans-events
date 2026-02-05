package com.urbanevents.priorizacion.service;

import com.urbanevents.priorizacion.domain.Prioridad;

import java.util.List;
/**
 * Servicio para calcular la prioridad de una incidencia basada en su descripción.
 * La prioridad se determina buscando palabras críticas en la descripción.
 * Si se encuentra alguna palabra crítica como "accidente" o "grave", la prioridad es CRITICA.
 * Si se encuentra otras palabras críticas, la prioridad es ALTA. De lo contrario, es MEDIA.
 * Este servicio es simple y se puede mejorar con técnicas más avanzadas como análisis de sentimientos o aprendizaje automático, pero cumple con el requisito básico de priorización basado en palabras clave.
 *  */
public class PriorizacionService {
    private final List<String> palabrasCriticas;
    private final int delaySegundos;

    public PriorizacionService(List<String> palabrasCriticas) {
        this.palabrasCriticas = palabrasCriticas;
        this.delaySegundos = 0;
    }

    public PriorizacionService(List<String> palabrasCriticas, int delaySegundos) {
        this.palabrasCriticas = palabrasCriticas;
        this.delaySegundos = delaySegundos;
    }

    public Prioridad calcularPrioridad(String descripcion) {
        aplicarDelay();
        if (descripcion == null) {
            return Prioridad.MEDIA;
        }
        String lower = descripcion.toLowerCase();
        // Palabras que indican prioridad CRITICA
        if (lower.contains("accidente") || lower.contains("grave")) {
            return Prioridad.CRITICA;
        }
        // Palabras que indican prioridad ALTA
        for (String palabra : palabrasCriticas) {
            if (lower.contains(palabra.toLowerCase())) {
                return Prioridad.ALTA;
            }
        }
        return Prioridad.MEDIA;
    }

    private void aplicarDelay() {
        if (delaySegundos > 0) {
            try {
                Thread.sleep(delaySegundos * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupt durante el procesamiento de priorización", e);
            }
        }
    }

    public String calcularMotivo(String descripcion) {
        if (descripcion == null) {
            return "prioridad por defecto";
        }
        String lower = descripcion.toLowerCase();
        // Palabras que indican prioridad CRITICA
        if (lower.contains("accidente") || lower.contains("grave")) {
            return "se ha encontrado palabra crítica 'accidente' o 'grave' en el campo descripcion";
        }
        // Palabras que indican prioridad ALTA
        for (String palabra : palabrasCriticas) {
            if (lower.contains(palabra.toLowerCase())) {
                return String.format("se ha encontrado literal '%s' en el campo descripcion", palabra);
            }
        }
        return "prioridad por defecto";
    }

    public int getDelaySegundos() {
        return delaySegundos;
    }
}
