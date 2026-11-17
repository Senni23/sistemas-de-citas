package com.clinica.citas.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductoRequest(
        @NotBlank String nombre,
        String descripcion,
        @NotNull Integer stock,
        @NotNull Integer stockMinimo,
        String unidadMedida,
        @NotNull BigDecimal precioUnitario,
        LocalDate fechaVencimiento
) {
}
