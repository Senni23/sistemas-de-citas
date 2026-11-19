package com.clinica.citas.application.pacientes.query;

import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.application.shared.Query;

import java.util.List;

/** Consultas de lectura de pacientes. */
public final class PacienteQueries {

    private PacienteQueries() {
    }

    public record ListarTodos() implements Query<List<PacienteView>> {
    }

    public record PorId(Long id) implements Query<PacienteView> {
    }
}
