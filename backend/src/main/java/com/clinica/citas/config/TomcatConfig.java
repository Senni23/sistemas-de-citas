package com.clinica.citas.config;

import org.apache.coyote.http11.Http11Nio2Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fuerza el conector NIO2 de Tomcat en lugar de NIO. En algunos entornos
 * Windows, el conector NIO por defecto falla al crear el Selector interno
 * ("Unable to establish loopback connection") por un problema del socket
 * de loopback AF_UNIX del propio JDK/SO. NIO2 usa IOCP en Windows y evita
 * esa ruta de codigo por completo.
 */
@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> nio2Connector() {
        return factory -> factory.setProtocol(Http11Nio2Protocol.class.getName());
    }
}
