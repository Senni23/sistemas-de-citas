package com.clinica.citas.infrastructure.security;

import com.clinica.citas.domain.identidad.PasswordHash;
import com.clinica.citas.domain.identidad.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Implementacion del puerto PasswordHasher del dominio con BCrypt. */
@Component
public class BcryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder encoder;

    public BcryptPasswordHasher(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public PasswordHash cifrar(String contrasenaPlana) {
        return new PasswordHash(encoder.encode(contrasenaPlana));
    }

    @Override
    public boolean coincide(String contrasenaPlana, PasswordHash hash) {
        return encoder.matches(contrasenaPlana, hash.valor());
    }
}
