package com.urbanevents.metricas.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanevents.events.IncidenciaChangedEvent;
import com.urbanevents.metricas.service.MetricasService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Consumidor de eventos: incidencias.modificadas
 * Escucha los eventos cuando cambia el estado de una incidencia
 * (resolución, cierre, rechazo, etc).
 */
@ApplicationScoped
public class IncidenciaChangedConsumer {

    private static final Logger LOG = Logger.getLogger(IncidenciaChangedConsumer.class);

    @Inject
    MetricasService metricasService;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Recibe mensajes del topic "incidencias-modificadas-metricas"
     * y los procesa deserializando a IncidenciaChangedEvent.
     */
    @Incoming("incidencias-modificadas-metricas")
    public void consume(String mensaje) {
        try {
            LOG.debugf("Recibido mensaje en incidencias-modificadas-metricas: %s", mensaje);

            // Deserializar el mensaje JSON al evento
            IncidenciaChangedEvent evento = objectMapper.readValue(mensaje, IncidenciaChangedEvent.class);

            // Procesar
            metricasService.procesarCambio(evento);

        } catch (Exception e) {
            LOG.errorf("Error procesando mensaje IncidenciaChangedEvent: %s. Mensaje: %s", 
                    e.getMessage(), mensaje);
            // Nota: El reintento se maneja vía configuración de Kafka en application.properties
        }
    }
}
