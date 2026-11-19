package com.clinica.citas.domain.pacientes;

import com.clinica.citas.domain.shared.DomainException;

import java.util.Objects;

/** Objeto de valor: el documento de identidad del paciente (obligatorio, hasta 15 caracteres). */
public final class Dni {

    private static final int MAXIMO = 15;

    private final String valor;

    private Dni(String valor) {
        this.valor = valor;
    }

    public static Dni de(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new DomainException("El DNI es obligatorio");
        }
        String limpio = texto.trim();
        if (limpio.length() > MAXIMO) {
            throw new DomainException("El DNI no puede pasar de " + MAXIMO + " caracteres");
        }
        return new Dni(limpio);
    }

    /** Reconstruye desde la base de datos sin validar. */
    public static Dni reconstruir(String texto) {
        return new Dni(texto);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof Dni d && Objects.equals(valor, d.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
