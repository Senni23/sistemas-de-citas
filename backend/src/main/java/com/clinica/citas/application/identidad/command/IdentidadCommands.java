package com.clinica.citas.application.identidad.command;

import com.clinica.citas.application.identidad.view.SesionView;
import com.clinica.citas.application.shared.Command;

public final class IdentidadCommands {

    private IdentidadCommands() {
    }

    public record IniciarSesion(String username, String password) implements Command<SesionView> {
    }

    /** Un paciente se registra solo: se crea el paciente y su cuenta, y queda con la sesion iniciada. */
    public record RegistrarPacienteConCuenta(String nombres, String apellidos, String dni, String telefono,
                                             String email, String direccion, String password) implements Command<SesionView> {
    }

    public record SolicitarRecuperacionPassword(String email) implements Command<Void> {
    }

    public record RestablecerPassword(String token, String passwordNueva) implements Command<Void> {
    }
}
