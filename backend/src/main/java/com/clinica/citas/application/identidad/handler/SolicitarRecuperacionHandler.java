package com.clinica.citas.application.identidad.handler;

import com.clinica.citas.application.identidad.NotificadorDeRecuperacion;
import com.clinica.citas.application.identidad.command.IdentidadCommands;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.identidad.TokenRecuperacion;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Caso de uso: pedir el enlace para restablecer la contrasena. Responde siempre igual, exista o no el
 * correo, para no revelar que usuarios existen; si el correo falla, el token igual queda guardado.
 */
@Service
public class SolicitarRecuperacionHandler implements CommandHandler<IdentidadCommands.SolicitarRecuperacionPassword, Void> {

    private static final Logger log = LoggerFactory.getLogger(SolicitarRecuperacionHandler.class);

    private final UsuarioRepository usuarios;
    private final NotificadorDeRecuperacion notificador;
    private final Clock reloj;

    public SolicitarRecuperacionHandler(UsuarioRepository usuarios, NotificadorDeRecuperacion notificador, Clock reloj) {
        this.usuarios = usuarios;
        this.notificador = notificador;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public Void handle(IdentidadCommands.SolicitarRecuperacionPassword c) {
        usuarios.buscarPorUsername(c.email()).ifPresent(usuario -> {
            TokenRecuperacion token = usuario.solicitarRecuperacion(UUID.randomUUID().toString(), LocalDateTime.now(reloj));
            usuarios.guardar(usuario);
            try {
                notificador.enviarEnlace(usuario.username(), token.valor());
            } catch (Exception ex) {
                log.warn("No se pudo enviar el correo de recuperacion a {}: {}", usuario.username(), ex.getMessage());
            }
        });
        return null;
    }
}
