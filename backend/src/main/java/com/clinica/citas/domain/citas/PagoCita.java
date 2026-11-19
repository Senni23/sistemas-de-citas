package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.shared.DomainException;

import java.time.LocalDateTime;

/** Objeto de valor: el pago (simulado) de una cita, con su metodo y la fecha en que se hizo. */
public record PagoCita(String metodo, LocalDateTime fecha) {

    public PagoCita {
        if (metodo == null || metodo.isBlank()) {
            throw new DomainException("El metodo de pago es obligatorio");
        }
        if (fecha == null) {
            throw new DomainException("La fecha del pago es obligatoria");
        }
        metodo = metodo.trim();
    }
}
