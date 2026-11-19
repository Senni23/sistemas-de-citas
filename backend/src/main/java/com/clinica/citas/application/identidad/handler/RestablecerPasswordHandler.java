package com.clinica.citas.application.identidad.handler;

import com.clinica.citas.application.identidad.command.IdentidadCommands;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.identidad.PasswordHasher;
import com.clinica.citas.domain.identidad.TokenInvalidoException;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/** Caso de uso: restablecer la contrasena con el token recibido por correo (vigente 1 hora y de un solo uso). */
@Service
public class RestablecerPasswordHandler implements CommandHandler<IdentidadCommands.RestablecerPassword, Void> {

    private final UsuarioRepository usuarios;
    private final PasswordHasher hasher;
    private final Clock reloj;

    public RestablecerPasswordHandler(UsuarioRepository usuarios, PasswordHasher hasher, Clock reloj) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public Void handle(IdentidadCommands.RestablecerPassword c) {
        Usuario usuario = usuarios.buscarPorTokenRecuperacion(c.token())
                .orElseThrow(() -> new TokenInvalidoException("El enlace de recuperacion no es valido"));
        usuario.restablecerPassword(hasher.cifrar(c.passwordNueva()), LocalDateTime.now(reloj));
        usuarios.guardar(usuario);
        return null;
    }
}
