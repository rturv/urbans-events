package com.urbanevents.metricas.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import com.urbanevents.metricas.service.MetricasService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Consumidor de eventos: incidencias.priorizadas
 * Escucha los eventos cuando se prioriza una incidencia.
 */
@ApplicationScoped
public class IncidenciaPriorizadaConsumer {

    private static final Logger LOG = Logger.getLogger(IncidenciaPriorizadaConsumer.class);

    @Inject
    MetricasService metricasService;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Recibe mensajes del topic "incidencias-priorizadas-metricas"
     * y los procesa deserializando a IncidenciaPriorizadaEvent.
     */
    @Incoming("incidencias-priorizadas-metricas")
    public void consume(String mensaje) {
        try {
            LOG.debugf("Recibido mensaje en incidencias-priorizadas-metricas: %s", mensaje);

            // Deserializar el mensaje JSON al evento
            IncidenciaPriorizadaEvent evento = objectMapper.readValue(mensaje, IncidenciaPriorizadaEvent.class);

            // Procesar
            metricasService.procesarPriorizacion(evento);

        } catch (Exception e) {
            LOG.errorf("Error procesando mensaje IncidenciaPriorizadaEvent: %s. Mensaje: %s", 
                    e.getMessage(), mensaje);
            // Nota: El reintento se maneja vía configuración de Kafka en application.properties
        }
    }
}
