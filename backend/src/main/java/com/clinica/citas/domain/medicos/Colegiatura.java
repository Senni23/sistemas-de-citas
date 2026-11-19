package com.clinica.citas.domain.medicos;

import com.clinica.citas.domain.shared.DomainException;

import java.util.Objects;

/** Objeto de valor: el numero de colegiatura del medico (obligatorio, hasta 20 caracteres). */
public final class Colegiatura {

    private static final int MAXIMO = 20;

    private final String valor;

    private Colegiatura(String valor) {
        this.valor = valor;
    }

    public static Colegiatura de(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new DomainException("La colegiatura es obligatoria");
        }
        String limpio = texto.trim();
        if (limpio.length() > MAXIMO) {
            throw new DomainException("La colegiatura no puede pasar de " + MAXIMO + " caracteres");
        }
        return new Colegiatura(limpio);
    }

    public static Colegiatura reconstruir(String texto) {
        return new Colegiatura(texto);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof Colegiatura c && Objects.equals(valor, c.valor);
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
