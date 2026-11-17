package com.clinica.citas.domain.catalogo;

import java.util.Objects;

/** Objeto de valor: identificador de la especialidad. Evita confundir ids de distintas entidades. */
public record EspecialidadId(Long valor) {

    public EspecialidadId {
        Objects.requireNonNull(valor, "El identificador es obligatorio");
    }
}
