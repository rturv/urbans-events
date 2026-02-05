package com.urbanevents.metricas.consumer;

import com.urbanevents.events.IncidenciaNotificadaEvent;
import com.urbanevents.metricas.domain.ResumenNotificaciones;
import com.urbanevents.metricas.domain.ResumenNotificacionesRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IncidenciaNotificadaConsumer {
    private final ResumenNotificacionesRepository repository;

    public IncidenciaNotificadaConsumer(ResumenNotificacionesRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "incidencias.notificadas", groupId = "metricas-notificadas",
            containerFactory = "notificadaListenerFactory")
    public void onMessage(IncidenciaNotificadaEvent event) {
        ResumenNotificaciones resumen = repository.findAll().stream().findFirst()
            .orElseGet(() -> new ResumenNotificaciones(null));
        resumen.incrementar();
        repository.save(resumen);
    }
}
