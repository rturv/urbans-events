package com.urbanevents.metricas.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.IncidenciaNotificadaEvent;
import com.urbanevents.metricas.service.MetricasService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Consumidor de eventos: incidencias.notificadas
 * Escucha los eventos cuando se notifica una incidencia.
 */
@ApplicationScoped
public class IncidenciaNotificadaConsumer {

    private static final Logger LOG = Logger.getLogger(IncidenciaNotificadaConsumer.class);

    @Inject
    MetricasService metricasService;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Recibe mensajes del topic "incidencias-notificadas-metricas"
     * y los procesa deserializando a IncidenciaNotificadaEvent.
     */
    @Incoming("incidencias-notificadas-metricas")
    public void consume(String mensaje) {
        try {
            LOG.debugf("Recibido mensaje en incidencias-notificadas-metricas: %s", mensaje);

            // Deserializar el mensaje JSON al evento
            IncidenciaNotificadaEvent evento = objectMapper.readValue(mensaje, IncidenciaNotificadaEvent.class);

            // Procesar
            metricasService.procesarNotificacion(evento);

        } catch (Exception e) {
            LOG.errorf("Error procesando mensaje IncidenciaNotificadaEvent: %s. Mensaje: %s", 
                    e.getMessage(), mensaje);
            // Nota: El reintento se maneja vía configuración de Kafka en application.properties
        }
    }
}
