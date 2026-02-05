package com.urbanevents.metricas.consumer;

import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.metricas.domain.Indicador;
import com.urbanevents.metricas.domain.IndicadorRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IncidenciaCreadaConsumer {
    private final IndicadorRepository repository;

    public IncidenciaCreadaConsumer(IndicadorRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "incidencias.creadas", groupId = "metricas-creadas",
            containerFactory = "creadaListenerFactory")
    public void onMessage(IncidenciaCreadaEvent event) {
        String tipo = event.tipo();

        Indicador indicador = repository.findByTipo(tipo)
            .orElseGet(() -> new Indicador(null, tipo));
        indicador.incrementarTotal();
        repository.save(indicador);
    }
}
