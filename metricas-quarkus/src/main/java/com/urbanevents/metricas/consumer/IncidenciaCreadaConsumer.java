package com.urbanevents.metricas.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.metricas.service.MetricasService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Consumidor de eventos: incidencias.creadas
 * Escucha los eventos cuando se crea una nueva incidencia.
 */
@ApplicationScoped
public class IncidenciaCreadaConsumer {

    private static final Logger LOG = Logger.getLogger(IncidenciaCreadaConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    MetricasService metricasService;

    /**
     * Recibe mensajes del topic "incidencias-creadas-metricas"
     * y los procesa deserializando a IncidenciaCreadaEvent.
     */
    @Incoming("incidencias-creadas-metricas")
    public void consume(String mensaje) {
        try {
            LOG.debugf("Recibido mensaje en incidencias-creadas-metricas: %s", mensaje);

            // Deserializar el mensaje JSON al evento
            IncidenciaCreadaEvent evento = objectMapper.readValue(mensaje, IncidenciaCreadaEvent.class);

            // Procesar
            metricasService.procesarCreacion(evento);

        } catch (Exception e) {
            LOG.errorf("Error procesando mensaje IncidenciaCreadaEvent: %s. Mensaje: %s", 
                    e.getMessage(), mensaje);
            // Nota: El reintento se maneja vía configuración de Kafka en application.properties
        }
    }
}
