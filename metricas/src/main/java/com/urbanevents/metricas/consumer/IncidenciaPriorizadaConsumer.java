package com.urbanevents.metricas.consumer;

import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.metricas.domain.Indicador;
import com.urbanevents.metricas.domain.IndicadorRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IncidenciaPriorizadaConsumer {
    private final IndicadorRepository repository;

    public IncidenciaPriorizadaConsumer(IndicadorRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "incidencias.priorizadas", groupId = "metricas-priorizadas",
            containerFactory = "priorizadaListenerFactory")
    public void onMessage(IncidenciaPriorizadaEvent event) {
        Indicador indicador = repository.findByTipo("prioridades")
            .orElseGet(() -> new Indicador(null, "prioridades"));
        indicador.incrementarPrioridad(event.prioridad());
        repository.save(indicador);
    }
}
