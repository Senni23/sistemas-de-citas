package com.clinica.citas.domain.citas;

import java.util.Objects;

/** Objeto de valor: identificador de la cita. Evita confundir ids de distintas entidades. */
public record CitaId(Long valor) {

    public CitaId {
        Objects.requireNonNull(valor, "El identificador es obligatorio");
    }
}
