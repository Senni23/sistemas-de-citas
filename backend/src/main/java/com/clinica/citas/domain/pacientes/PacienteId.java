package com.clinica.citas.domain.pacientes;

import java.util.Objects;

/** Objeto de valor: identificador de el paciente. Evita confundir ids de distintas entidades. */
public record PacienteId(Long valor) {

    public PacienteId {
        Objects.requireNonNull(valor, "El identificador es obligatorio");
    }
}
