package com.clinica.citas.dto;

public record AuthResponse(
        String token,
        String username,
        String rol,
        String nombre,
        Long entidadId
) {
}
