package com.clinica.citas.dto;

import jakarta.validation.constraints.NotBlank;

public record OlvidePasswordRequest(
        @NotBlank String email
) {
}
