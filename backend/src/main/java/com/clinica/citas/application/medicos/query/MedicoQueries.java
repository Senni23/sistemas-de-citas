package com.clinica.citas.application.medicos.query;

import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.application.shared.Query;

import java.util.List;

public final class MedicoQueries {

    private MedicoQueries() {
    }

    public record ListarTodos() implements Query<List<MedicoView>> {
    }

    public record PorId(Long id) implements Query<MedicoView> {
    }
}
