package com.clinica.citas.domain.farmacia;

import com.clinica.citas.domain.shared.DomainException;

import java.util.Objects;

/** Objeto de valor: la cantidad disponible de un producto y el minimo que debe haber. */
public final class Stock {

    private final int actual;
    private final int minimo;

    private Stock(int actual, int minimo) {
        this.actual = actual;
        this.minimo = minimo;
    }

    public static Stock de(int actual, int minimo) {
        if (actual < 0 || minimo < 0) {
            throw new DomainException("El stock y el stock minimo no pueden ser negativos");
        }
        return new Stock(actual, minimo);
    }

    /** Reconstruye desde la base de datos sin validar. */
    public static Stock reconstruir(int actual, int minimo) {
        return new Stock(actual, minimo);
    }

    /** Hay que reponer cuando lo disponible llega al minimo o baja de el. */
    public boolean estaBajoElMinimo() {
        return actual <= minimo;
    }

    public int actual() {
        return actual;
    }

    public int minimo() {
        return minimo;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof Stock s && actual == s.actual && minimo == s.minimo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(actual, minimo);
    }
}
