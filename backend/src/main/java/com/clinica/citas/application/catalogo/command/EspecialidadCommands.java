package com.clinica.citas.application.catalogo.command;

import com.clinica.citas.application.catalogo.view.EspecialidadView;
import com.clinica.citas.application.shared.Command;

public final class EspecialidadCommands {

    private EspecialidadCommands() {
    }

    public record Crear(String nombre, String descripcion) implements Command<EspecialidadView> {
    }

    public record Actualizar(Long id, String nombre, String descripcion) implements Command<EspecialidadView> {
    }

    public record Eliminar(Long id) implements Command<Void> {
    }
}
