package com.urbanevents.priorizacion.service;

import java.util.List;

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
}
