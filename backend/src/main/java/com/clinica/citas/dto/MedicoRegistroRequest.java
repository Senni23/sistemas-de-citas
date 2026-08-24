package com.clinica.citas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicoRegistroRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank String colegiatura,
        String telefono,
        @NotBlank @Email String email,
        @NotNull Long especialidadId,
        @NotBlank @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres") String password
) {
}
