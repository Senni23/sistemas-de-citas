package com.clinica.citas.domain.medicos;

import java.util.Objects;

/** Objeto de valor: identificador de el medico. Evita confundir ids de distintas entidades. */
public record MedicoId(Long valor) {

    public MedicoId {
        Objects.requireNonNull(valor, "El identificador es obligatorio");
    }
}
