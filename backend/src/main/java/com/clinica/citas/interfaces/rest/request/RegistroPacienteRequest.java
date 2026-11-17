package com.clinica.citas.interfaces.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroPacienteRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank String dni,
        String telefono,
        @NotBlank @Email String email,
        String direccion,
        @NotBlank @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres") String password
) {
}
