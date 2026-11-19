package com.clinica.citas.application.identidad.handler;

import com.clinica.citas.application.identidad.ConstructorDeSesion;
import com.clinica.citas.application.identidad.command.IdentidadCommands;
import com.clinica.citas.application.identidad.view.SesionView;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.identidad.CredencialesInvalidasException;
import com.clinica.citas.domain.identidad.PasswordHasher;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: iniciar sesion. Un usuario inexistente y una clave incorrecta dan el mismo error. */
@Service
public class IniciarSesionHandler implements CommandHandler<IdentidadCommands.IniciarSesion, SesionView> {

    private static final String MENSAJE = "Usuario o contrasena incorrectos";

    private final UsuarioRepository usuarios;
    private final PasswordHasher hasher;
    private final ConstructorDeSesion sesiones;

    public IniciarSesionHandler(UsuarioRepository usuarios, PasswordHasher hasher, ConstructorDeSesion sesiones) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.sesiones = sesiones;
    }

    @Override
    @Transactional(readOnly = true)
    public SesionView handle(IdentidadCommands.IniciarSesion c) {
        Usuario usuario = usuarios.buscarPorUsername(c.username())
                .orElseThrow(() -> new CredencialesInvalidasException(MENSAJE));
        if (!hasher.coincide(c.password(), usuario.passwordHash())) {
            throw new CredencialesInvalidasException(MENSAJE);
        }
        return sesiones.construir(usuario);
    }
}
