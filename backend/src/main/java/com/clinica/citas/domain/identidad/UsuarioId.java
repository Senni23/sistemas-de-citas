package com.clinica.citas.domain.identidad;

import java.util.Objects;

/** Objeto de valor: identificador de el usuario. Evita confundir ids de distintas entidades. */
public record UsuarioId(Long valor) {

    public UsuarioId {
        Objects.requireNonNull(valor, "El identificador es obligatorio");
    }
}
