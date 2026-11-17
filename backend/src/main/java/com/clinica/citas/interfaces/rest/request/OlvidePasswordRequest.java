package com.clinica.citas.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;

public record OlvidePasswordRequest(
        @NotBlank String email
) {
}
