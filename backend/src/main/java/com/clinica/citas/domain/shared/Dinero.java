package com.clinica.citas.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Objeto de valor: una cantidad de dinero en soles, nunca negativa y con 2 decimales. */
public record Dinero(BigDecimal valor) {

    public Dinero {
        if (valor == null) {
            throw new DomainException("El monto es obligatorio");
        }
        if (valor.signum() < 0) {
            throw new DomainException("El monto no puede ser negativo");
        }
        valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinero de(String texto) {
        return new Dinero(new BigDecimal(texto));
    }

    /** Reconstruye desde la base de datos sin volver a validar (los datos ya fueron aceptados antes). */
    public static Dinero reconstruir(BigDecimal valor) {
        return valor == null ? null : new Dinero(valor.max(BigDecimal.ZERO));
    }
}
