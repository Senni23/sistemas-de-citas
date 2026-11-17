package com.clinica.citas.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PacienteRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank String dni,
        LocalDate fechaNacimiento,
        String telefono,
        String email,
        String direccion
) {
}
