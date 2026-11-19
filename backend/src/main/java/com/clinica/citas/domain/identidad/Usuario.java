package com.clinica.citas.domain.identidad;

import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.DomainException;

import java.time.LocalDateTime;

/**
 * Raiz de agregado: la cuenta de acceso de una persona. Guarda siempre la contrasena cifrada y es
 * duena de la regla de recuperacion de contrasena: el token dura 1 hora y sirve una sola vez.
 */
public final class Usuario {

    private static final int MAXIMO_USERNAME = 100;

    private final UsuarioId id;
    private final String username;
    private PasswordHash passwordHash;
    private final Rol rol;
    private final MedicoId medicoId;
    private final PacienteId pacienteId;
    private TokenRecuperacion tokenRecuperacion;

    private Usuario(UsuarioId id, String username, PasswordHash passwordHash, Rol rol, MedicoId medicoId,
                    PacienteId pacienteId, TokenRecuperacion tokenRecuperacion) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.medicoId = medicoId;
        this.pacienteId = pacienteId;
        this.tokenRecuperacion = tokenRecuperacion;
    }

    public static Usuario administrador(String username, PasswordHash passwordHash) {
        return new Usuario(null, validarUsername(username), exigirHash(passwordHash), Rol.ADMIN, null, null, null);
    }

    public static Usuario paraPaciente(String username, PasswordHash passwordHash, PacienteId pacienteId) {
        if (pacienteId == null) {
            throw new DomainException("La cuenta de paciente necesita el paciente asociado");
        }
        return new Usuario(null, validarUsername(username), exigirHash(passwordHash), Rol.PACIENTE, null, pacienteId, null);
    }

    public static Usuario paraMedico(String username, PasswordHash passwordHash, MedicoId medicoId) {
        if (medicoId == null) {
            throw new DomainException("La cuenta de medico necesita el medico asociado");
        }
        return new Usuario(null, validarUsername(username), exigirHash(passwordHash), Rol.MEDICO, medicoId, null, null);
    }

    public static Usuario reconstruir(UsuarioId id, String username, PasswordHash passwordHash, Rol rol, MedicoId medicoId,
                                      PacienteId pacienteId, TokenRecuperacion tokenRecuperacion) {
        return new Usuario(id, username, passwordHash, rol, medicoId, pacienteId, tokenRecuperacion);
    }

    /** Genera el token de recuperacion (vigente 1 hora) y lo guarda en la cuenta. */
    public TokenRecuperacion solicitarRecuperacion(String tokenGenerado, LocalDateTime ahora) {
        this.tokenRecuperacion = TokenRecuperacion.emitir(tokenGenerado, ahora);
        return this.tokenRecuperacion;
    }

    /** Cambia la contrasena si hay un token vigente; despues el token se invalida (un solo uso). */
    public void restablecerPassword(PasswordHash nuevoHash, LocalDateTime ahora) {
        if (tokenRecuperacion == null) {
            throw new TokenInvalidoException("El enlace de recuperacion no es valido");
        }
        if (!tokenRecuperacion.estaVigente(ahora)) {
            throw new TokenInvalidoException("El enlace de recuperacion ha expirado, solicita uno nuevo");
        }
        this.passwordHash = exigirHash(nuevoHash);
        this.tokenRecuperacion = null;
    }

    public void cambiarPassword(PasswordHash nuevoHash) {
        this.passwordHash = exigirHash(nuevoHash);
    }

    private static String validarUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new DomainException("El usuario es obligatorio");
        }
        if (username.trim().length() > MAXIMO_USERNAME) {
            throw new DomainException("El usuario no puede pasar de " + MAXIMO_USERNAME + " caracteres");
        }
        return username.trim();
    }

    private static PasswordHash exigirHash(PasswordHash hash) {
        if (hash == null) {
            throw new DomainException("La contrasena es obligatoria");
        }
        return hash;
    }

    public UsuarioId id() {
        return id;
    }

    public String username() {
        return username;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public Rol rol() {
        return rol;
    }

    public MedicoId medicoId() {
        return medicoId;
    }

    public PacienteId pacienteId() {
        return pacienteId;
    }

    public TokenRecuperacion tokenRecuperacion() {
        return tokenRecuperacion;
    }
}
