package com.clinica.citas.application.pacientes.command;

import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.application.shared.Command;

import java.time.LocalDate;

/** Ordenes que cambian los datos de pacientes. */
public final class PacienteCommands {

    private PacienteCommands() {
    }

    public record Registrar(String nombres, String apellidos, String dni, LocalDate fechaNacimiento,
                            String telefono, String email, String direccion) implements Command<PacienteView> {
    }

    public record Actualizar(Long id, String nombres, String apellidos, String dni, LocalDate fechaNacimiento,
                             String telefono, String email, String direccion) implements Command<PacienteView> {
    }

    public record Eliminar(Long id) implements Command<Void> {
    }
}
