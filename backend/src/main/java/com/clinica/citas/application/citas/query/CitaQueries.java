package com.clinica.citas.application.citas.query;

import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.shared.PaginaView;
import com.clinica.citas.application.shared.Query;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.OrdenDeCitas;

import java.util.List;

/** Consultas de lectura de la agenda. */
public final class CitaQueries {

    private CitaQueries() {
    }

    public record ListarTodas() implements Query<List<CitaView>> {
    }

    /** Listado de a una pagina; {@code estado} puede ser null para no filtrar. */
    public record Buscar(EstadoCita estado, OrdenDeCitas orden, boolean descendente, int pagina, int tamano)
            implements Query<PaginaView<CitaView>> {
    }

    public record PorId(Long id) implements Query<CitaView> {
    }

    public record PorPaciente(Long pacienteId) implements Query<List<CitaView>> {
    }

    public record PorMedico(Long medicoId) implements Query<List<CitaView>> {
    }
}
