package com.clinica.citas.application.medicos.command;

import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.application.shared.Command;

public final class MedicoCommands {

    private MedicoCommands() {
    }

    /** Registra un medico y crea tambien su cuenta de acceso (usuario = su correo). */
    public record Registrar(String nombres, String apellidos, String colegiatura, String telefono, String email,
                            Long especialidadId, String password) implements Command<MedicoView> {
    }

    /** Si especialidadId es null, el medico conserva su especialidad actual. */
    public record Actualizar(Long id, String nombres, String apellidos, String colegiatura, String telefono,
                             String email, Long especialidadId) implements Command<MedicoView> {
    }

    public record Eliminar(Long id) implements Command<Void> {
    }
}
