package com.clinica.citas.domain.identidad;

import com.clinica.citas.domain.shared.DomainException;

/** Objeto de valor: una contrasena ya cifrada. Nunca se guarda ni se muestra la contrasena en texto plano. */
public record PasswordHash(String valor) {

    public PasswordHash {
        if (valor == null || valor.isBlank()) {
            throw new DomainException("La contrasena cifrada no puede estar vacia");
        }
    }

    /** Detecta hashes BCrypt, para re-cifrar cuentas antiguas que tenian la clave en texto plano. */
    public boolean esBcrypt() {
        return valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$");
    }

    @Override
    public String toString() {
        return "PasswordHash[****]";
    }
}
