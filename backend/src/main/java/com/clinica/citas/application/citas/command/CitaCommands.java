package com.clinica.citas.application.citas.command;

import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.shared.Command;
import com.clinica.citas.domain.citas.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

/** Ordenes que cambian la agenda de citas. */
public final class CitaCommands {

    private CitaCommands() {
    }

    /** servicioId puede ser null (cita sin servicio asociado). */
    public record Agendar(Long pacienteId, Long medicoId, Long servicioId, LocalDate fecha, LocalTime hora,
                          String motivo) implements Command<CitaView> {
    }

    public record Reprogramar(Long id, Long pacienteId, Long medicoId, LocalDate fecha, LocalTime hora,
                              String motivo) implements Command<CitaView> {
    }

    public record CambiarEstado(Long id, EstadoCita estado) implements Command<CitaView> {
    }

    public record Pagar(Long id, String metodoPago) implements Command<CitaView> {
    }

    public record Eliminar(Long id) implements Command<Void> {
    }
}
