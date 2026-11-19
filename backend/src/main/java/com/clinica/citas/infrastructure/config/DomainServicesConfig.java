package com.clinica.citas.infrastructure.config;

import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.DisponibilidadMedico;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registra los servicios de dominio (clases puras, sin anotaciones de Spring) como beans. */
@Configuration
public class DomainServicesConfig {

    @Bean
    public DisponibilidadMedico disponibilidadMedico(CitaRepository citas) {
        return new DisponibilidadMedico(citas);
    }
}
