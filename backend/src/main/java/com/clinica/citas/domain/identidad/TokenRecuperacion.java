package com.clinica.citas.domain.identidad;

import com.clinica.citas.domain.shared.DomainException;

import java.time.Duration;
import java.time.LocalDateTime;

/** Objeto de valor: el token que se envia por correo para restablecer la contrasena. Dura 1 hora. */
public record TokenRecuperacion(String valor, LocalDateTime expira) {

    public static final Duration VIGENCIA = Duration.ofHours(1);

    public TokenRecuperacion {
        if (valor == null || valor.isBlank()) {
            throw new DomainException("El token de recuperacion es obligatorio");
        }
    }

    public static TokenRecuperacion emitir(String valor, LocalDateTime ahora) {
        return new TokenRecuperacion(valor, ahora.plus(VIGENCIA));
    }

    public boolean estaVigente(LocalDateTime ahora) {
        return expira != null && !expira.isBefore(ahora);
    }
}
