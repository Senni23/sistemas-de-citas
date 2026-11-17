package com.clinica.citas.application.citas.query;

import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.shared.Query;

import java.util.List;

/** Consultas de lectura de la agenda. */
public final class CitaQueries {

    private CitaQueries() {
    }

    public record ListarTodas() implements Query<List<CitaView>> {
    }

    public record PorId(Long id) implements Query<CitaView> {
    }

    public record PorPaciente(Long pacienteId) implements Query<List<CitaView>> {
    }

    public record PorMedico(Long medicoId) implements Query<List<CitaView>> {
    }
}
