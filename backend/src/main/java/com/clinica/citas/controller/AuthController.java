package com.clinica.citas.controller;

import com.clinica.citas.dto.AuthResponse;
import com.clinica.citas.dto.LoginRequest;
import com.clinica.citas.dto.OlvidePasswordRequest;
import com.clinica.citas.dto.RegistroPacienteRequest;
import com.clinica.citas.dto.RestablecerPasswordRequest;
import com.clinica.citas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registro(@Valid @RequestBody RegistroPacienteRequest request) {
        return authService.registrarPaciente(request);
    }

    @PostMapping("/olvide-password")
    public Map<String, String> olvidePassword(@Valid @RequestBody OlvidePasswordRequest request) {
        authService.solicitarRecuperacion(request.email());
        return Map.of("mensaje", "Si el correo esta registrado, te enviamos un enlace para restablecer tu contrasena");
    }

    @PostMapping("/restablecer-password")
    public Map<String, String> restablecerPassword(@Valid @RequestBody RestablecerPasswordRequest request) {
        authService.restablecerPassword(request.token(), request.passwordNueva());
        return Map.of("mensaje", "Tu contrasena se actualizo correctamente, ya puedes iniciar sesion");
    }
}
