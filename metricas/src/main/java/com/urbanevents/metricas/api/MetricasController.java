package com.urbanevents.metricas.api;

import com.urbanevents.metricas.domain.IndicadorRepository;
import com.urbanevents.metricas.domain.ResumenNotificacionesRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/metricas")
public class MetricasController {
    private final IndicadorRepository repository;
    private final ResumenNotificacionesRepository resumenRepository;

    public MetricasController(IndicadorRepository repository,
                              ResumenNotificacionesRepository resumenRepository) {
        this.repository = repository;
        this.resumenRepository = resumenRepository;
    }

    @GetMapping
    public ResumenResponse listar() {
        Map<String, Long> porTipo = new LinkedHashMap<>();
        Map<String, Long> prioridades = new LinkedHashMap<>();

        repository.findAll().forEach(indicador -> {
            if ("prioridades".equalsIgnoreCase(indicador.getTipo())) {
                prioridades.put("alta", indicador.getPrioridadAlta());
                prioridades.put("media", indicador.getPrioridadMedia());
                prioridades.put("baja", indicador.getPrioridadBaja());
            } else {
                porTipo.put(indicador.getTipo(), indicador.getTotal());
            }
        });

        long notificaciones = resumenRepository.findAll().stream().findFirst()
            .map(resumen -> resumen.getTotal())
            .orElse(0L);

        return new ResumenResponse(porTipo, prioridades, notificaciones);
    }
}
