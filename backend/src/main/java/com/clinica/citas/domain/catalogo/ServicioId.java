package com.clinica.citas.domain.catalogo;

import java.util.Objects;

/** Objeto de valor: identificador de el servicio. Evita confundir ids de distintas entidades. */
public record ServicioId(Long valor) {

    public ServicioId {
        Objects.requireNonNull(valor, "El identificador es obligatorio");
    }
}
