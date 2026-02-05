package com.urbanevents.priorizacion.config;

import com.urbanevents.priorizacion.service.PriorizacionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class PriorizacionConfig {
    @Bean
    public PriorizacionService priorizacionService(@Value("${priorizacion.palabras-criticas}") String palabras) {
        List<String> lista = Arrays.stream(palabras.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
        return new PriorizacionService(lista);
    }
}
