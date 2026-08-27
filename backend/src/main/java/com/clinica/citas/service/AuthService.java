package com.clinica.citas.service;

import com.clinica.citas.dto.AuthResponse;
import com.clinica.citas.dto.LoginRequest;
import com.clinica.citas.dto.RegistroPacienteRequest;
import com.clinica.citas.exception.CredencialesInvalidasException;
import com.clinica.citas.exception.RegistroDuplicadoException;
import com.clinica.citas.exception.TokenInvalidoException;
import com.clinica.citas.model.Paciente;
import com.clinica.citas.model.Rol;
import com.clinica.citas.model.Usuario;
import com.clinica.citas.repository.PacienteRepository;
import com.clinica.citas.repository.UsuarioRepository;
import com.clinica.citas.security.JwtService;
import com.clinica.citas.security.UsuarioPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(UsuarioRepository usuarioRepository, PacienteRepository pacienteRepository,
                        PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contrasena incorrectos"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new CredencialesInvalidasException("Usuario o contrasena incorrectos");
        }

        return construirRespuesta(usuario);
    }

    @Transactional
    public AuthResponse registrarPaciente(RegistroPacienteRequest request) {
        if (pacienteRepository.findByDni(request.dni()).isPresent()) {
            throw new RegistroDuplicadoException("Ya existe un paciente registrado con ese DNI");
        }
        if (usuarioRepository.findByUsername(request.email()).isPresent()) {
            throw new RegistroDuplicadoException("Ya existe una cuenta con ese correo");
        }

        Paciente paciente = new Paciente();
        paciente.setNombres(request.nombres());
        paciente.setApellidos(request.apellidos());
        paciente.setDni(request.dni());
        paciente.setTelefono(request.telefono());
        paciente.setEmail(request.email());
        paciente.setDireccion(request.direccion());
        paciente = pacienteRepository.save(paciente);

        Usuario usuario = new Usuario();
        usuario.setUsername(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(Rol.PACIENTE);
        usuario.setPaciente(paciente);
        usuario = usuarioRepository.save(usuario);

        return construirRespuesta(usuario);
    }

    @Transactional
    public void solicitarRecuperacion(String email) {
        usuarioRepository.findByUsername(email).ifPresent(usuario -> {
            usuario.setResetToken(UUID.randomUUID().toString());
            usuario.setResetTokenExpira(LocalDateTime.now().plusHours(1));
            usuarioRepository.save(usuario);
            try {
                emailService.enviarCorreoRecuperacion(usuario.getUsername(), usuario.getResetToken());
            } catch (Exception ex) {
                // El token ya quedo guardado; si el envio de correo falla (p.ej. SMTP mal configurado)
                // no debe romper la respuesta generica ni revertir el token.
                log.warn("No se pudo enviar el correo de recuperacion a {}: {}", usuario.getUsername(), ex.getMessage());
            }
        });
        // Siempre responde igual, exista o no el correo, para no revelar que usuarios existen.
    }

    @Transactional
    public void restablecerPassword(String token, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByResetToken(token)
                .orElseThrow(() -> new TokenInvalidoException("El enlace de recuperacion no es valido"));

        if (usuario.getResetTokenExpira() == null || usuario.getResetTokenExpira().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidoException("El enlace de recuperacion ha expirado, solicita uno nuevo");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuario.setResetToken(null);
        usuario.setResetTokenExpira(null);
        usuarioRepository.save(usuario);
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        String token = jwtService.generarToken(principal);
        Long entidadId = usuario.getRol() == Rol.MEDICO ? principal.getMedicoId()
                : usuario.getRol() == Rol.PACIENTE ? principal.getPacienteId()
                : null;
        String nombre = usuario.getMedico() != null
                ? usuario.getMedico().getNombres() + " " + usuario.getMedico().getApellidos()
                : usuario.getPaciente() != null
                ? usuario.getPaciente().getNombres() + " " + usuario.getPaciente().getApellidos()
                : usuario.getUsername();

        return new AuthResponse(token, usuario.getUsername(), usuario.getRol().name(), nombre, entidadId);
    }
}
