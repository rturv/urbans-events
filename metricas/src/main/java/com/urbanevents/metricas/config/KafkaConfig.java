package com.urbanevents.metricas.config;

import com.urbanevents.events.IncidenciaCreadaEvent;
import com.urbanevents.events.IncidenciaNotificadaEvent;
import com.urbanevents.events.IncidenciaPriorizadaEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Bean
    public ConsumerFactory<String, IncidenciaCreadaEvent> creadaConsumerFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "metricas-creadas");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(IncidenciaCreadaEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IncidenciaCreadaEvent> creadaListenerFactory(
            ConsumerFactory<String, IncidenciaCreadaEvent> creadaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, IncidenciaCreadaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(creadaConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, IncidenciaPriorizadaEvent> priorizadaConsumerFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "metricas-priorizadas");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(IncidenciaPriorizadaEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IncidenciaPriorizadaEvent> priorizadaListenerFactory(
            ConsumerFactory<String, IncidenciaPriorizadaEvent> priorizadaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, IncidenciaPriorizadaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(priorizadaConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, IncidenciaNotificadaEvent> notificadaConsumerFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "metricas-notificadas");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(IncidenciaNotificadaEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IncidenciaNotificadaEvent> notificadaListenerFactory(
            ConsumerFactory<String, IncidenciaNotificadaEvent> notificadaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, IncidenciaNotificadaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(notificadaConsumerFactory);
        return factory;
    }
}
