package com.clinica.citas.interfaces.rest.controller;

import com.clinica.citas.application.identidad.command.IdentidadCommands;
import com.clinica.citas.application.identidad.handler.IniciarSesionHandler;
import com.clinica.citas.application.identidad.handler.RegistrarPacienteConCuentaHandler;
import com.clinica.citas.application.identidad.handler.RestablecerPasswordHandler;
import com.clinica.citas.application.identidad.handler.SolicitarRecuperacionHandler;
import com.clinica.citas.application.identidad.view.SesionView;
import com.clinica.citas.interfaces.rest.request.LoginRequest;
import com.clinica.citas.interfaces.rest.request.OlvidePasswordRequest;
import com.clinica.citas.interfaces.rest.request.RegistroPacienteRequest;
import com.clinica.citas.interfaces.rest.request.RestablecerPasswordRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Puerta de entrada de la identidad: login, registro de pacientes y recuperacion de contrasena. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IniciarSesionHandler iniciarSesion;
    private final RegistrarPacienteConCuentaHandler registrarPaciente;
    private final SolicitarRecuperacionHandler solicitarRecuperacion;
    private final RestablecerPasswordHandler restablecerPassword;

    public AuthController(IniciarSesionHandler iniciarSesion, RegistrarPacienteConCuentaHandler registrarPaciente,
                          SolicitarRecuperacionHandler solicitarRecuperacion, RestablecerPasswordHandler restablecerPassword) {
        this.iniciarSesion = iniciarSesion;
        this.registrarPaciente = registrarPaciente;
        this.solicitarRecuperacion = solicitarRecuperacion;
        this.restablecerPassword = restablecerPassword;
    }

    @PostMapping("/login")
    public SesionView login(@Valid @RequestBody LoginRequest request) {
        return iniciarSesion.handle(new IdentidadCommands.IniciarSesion(request.username(), request.password()));
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public SesionView registro(@Valid @RequestBody RegistroPacienteRequest request) {
        return registrarPaciente.handle(new IdentidadCommands.RegistrarPacienteConCuenta(request.nombres(),
                request.apellidos(), request.dni(), request.telefono(), request.email(), request.direccion(), request.password()));
    }

    @PostMapping("/olvide-password")
    public Map<String, String> olvidePassword(@Valid @RequestBody OlvidePasswordRequest request) {
        solicitarRecuperacion.handle(new IdentidadCommands.SolicitarRecuperacionPassword(request.email()));
        return Map.of("mensaje", "Si el correo esta registrado, te enviamos un enlace para restablecer tu contrasena");
    }

    @PostMapping("/restablecer-password")
    public Map<String, String> restablecerPassword(@Valid @RequestBody RestablecerPasswordRequest request) {
        restablecerPassword.handle(new IdentidadCommands.RestablecerPassword(request.token(), request.passwordNueva()));
        return Map.of("mensaje", "Tu contrasena se actualizo correctamente, ya puedes iniciar sesion");
    }
}
