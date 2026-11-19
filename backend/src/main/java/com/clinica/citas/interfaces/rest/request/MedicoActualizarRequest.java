package com.clinica.citas.interfaces.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MedicoActualizarRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank String colegiatura,
        String telefono,
        @Email String email,
        @Valid IdRef especialidad
) {
}
