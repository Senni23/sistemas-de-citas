package com.clinica.citas.application.catalogo.query;

import com.clinica.citas.application.catalogo.view.EspecialidadView;
import com.clinica.citas.application.catalogo.view.ServicioView;
import com.clinica.citas.application.shared.Query;

import java.util.List;

public final class CatalogoQueries {

    private CatalogoQueries() {
    }

    public record ListarEspecialidades() implements Query<List<EspecialidadView>> {
    }

    public record ObtenerEspecialidad(Long id) implements Query<EspecialidadView> {
    }

    /** Si especialidadId es null, lista todos los servicios; si no, solo los de esa especialidad. */
    public record ListarServicios(Long especialidadId) implements Query<List<ServicioView>> {
    }
}
