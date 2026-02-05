package com.urbanevents.priorizacion.service;

import java.util.List;
/**
 * Servicio para calcular la prioridad de una incidencia basada en su descripción.
 * La prioridad se determina buscando palabras críticas en la descripción.
 * Si se encuentra alguna palabra crítica, la prioridad es "alta". De lo contrario, es "media".
 * Este servicio es simple y se puede mejorar con técnicas más avanzadas como análisis de sentimientos o aprendizaje automático, pero cumple con el requisito básico de priorización basado en palabras clave.
 *  */
public class PriorizacionService {
    private final List<String> palabrasCriticas;

    public PriorizacionService(List<String> palabrasCriticas) {
        this.palabrasCriticas = palabrasCriticas;
    }

    public String calcularPrioridad(String descripcion) {
        if (descripcion == null) {
            return "media";
        }
        String lower = descripcion.toLowerCase();
        for (String palabra : palabrasCriticas) {
            if (lower.contains(palabra.toLowerCase())) {
                return "alta";
            }
        }
        return "media";
    }

    public String calcularMotivo(String descripcion) {
        if (descripcion == null) {
            return "prioridad por defecto";
        }
        String lower = descripcion.toLowerCase();
        for (String palabra : palabrasCriticas) {
            if (lower.contains(palabra.toLowerCase())) {
                return String.format("se ha encontrado literal '%s' en el campo descripcion", palabra);
            }
        }
        return "prioridad por defecto";
    }
}
