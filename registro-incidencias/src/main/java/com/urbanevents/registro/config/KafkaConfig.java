package com.urbanevents.registro.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring Cloud Stream para registro-incidencias.
 * La mayoría de la configuración se encuentra en application.yml
 */
@Configuration
public class KafkaConfig {
    // La configuración de bindings y serialización se maneja a través de application.yml
    // Spring Cloud Stream automáticamente maneja la serialización basada en los tipos de las functions
}

