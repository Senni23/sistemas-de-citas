package com.clinica.citas.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** El reloj se inyecta (en vez de llamar a now() por todos lados) para poder controlar el tiempo en las pruebas. */
@Configuration
public class ClockConfig {

    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }
}
