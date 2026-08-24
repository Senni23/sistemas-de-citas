package com.clinica.citas.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sin esto, Jackson no sabe convertir a JSON los proxies "lazy" que crea Hibernate para las
 * relaciones @ManyToOne (por ejemplo Medico.especialidad o Cita.paciente/medico), y la respuesta
 * truena a mitad de camino. Este modulo le ensena a Jackson a serializarlos como el objeto real.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();
        // Sin esto, una relacion lazy que aun no se cargo se serializa como null en vez de sus datos reales.
        module.enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        return module;
    }
}
