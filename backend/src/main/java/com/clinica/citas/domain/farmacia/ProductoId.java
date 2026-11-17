package com.clinica.citas.domain.farmacia;

import java.util.Objects;

/** Objeto de valor: identificador de el producto. Evita confundir ids de distintas entidades. */
public record ProductoId(Long valor) {

    public ProductoId {
        Objects.requireNonNull(valor, "El identificador es obligatorio");
    }
}
