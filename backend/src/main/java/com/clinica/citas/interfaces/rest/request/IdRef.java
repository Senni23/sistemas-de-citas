package com.clinica.citas.interfaces.rest.request;

import jakarta.validation.constraints.NotNull;

/** Referencia a otro registro por su id, por ejemplo {"id": 5}. */
public record IdRef(@NotNull Long id) {
}
