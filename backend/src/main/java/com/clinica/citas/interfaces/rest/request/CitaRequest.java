package com.clinica.citas.interfaces.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaRequest(
        @NotNull @Valid IdRef paciente,
        @NotNull @Valid IdRef medico,
        @Valid IdRef servicio,
        @NotNull LocalDate fecha,
        @NotNull LocalTime hora,
        String motivo
) {
}
