package com.clinica.citas.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;

public record EspecialidadRequest(
        @NotBlank String nombre,
        String descripcion
) {
}
