package com.clinica.citas.application.citas.view;

import com.clinica.citas.application.catalogo.view.ServicioView;
import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.domain.citas.EstadoCita;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Lo que se devuelve al exterior de una cita, ya con los datos del paciente, el medico y el servicio. */
public record CitaView(Long id, PacienteView paciente, MedicoView medico, ServicioView servicio, LocalDate fecha,
                       LocalTime hora, String motivo, EstadoCita estado, boolean pagado, BigDecimal montoPagado,
                       String metodoPago, LocalDateTime fechaPago) {
}
