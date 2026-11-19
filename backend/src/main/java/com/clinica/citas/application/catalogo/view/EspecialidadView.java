package com.clinica.citas.application.catalogo.view;

import com.clinica.citas.domain.catalogo.Especialidad;

public record EspecialidadView(Long id, String nombre, String descripcion) {

    public static EspecialidadView de(Especialidad e) {
        return new EspecialidadView(e.id().valor(), e.nombre(), e.descripcion());
    }
}
