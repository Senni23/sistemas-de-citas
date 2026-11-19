package com.clinica.citas.application.pacientes.view;

import com.clinica.citas.domain.pacientes.Paciente;

import java.time.LocalDate;

/** Lo que se devuelve al exterior de un paciente. */
public record PacienteView(Long id, String nombres, String apellidos, String dni, LocalDate fechaNacimiento,
                           String telefono, String email, String direccion) {

    public static PacienteView de(Paciente p) {
        return new PacienteView(p.id().valor(), p.nombre().nombres(), p.nombre().apellidos(), p.dni().valor(),
                p.fechaNacimiento(), p.telefono(), p.correo() != null ? p.correo().valor() : null, p.direccion());
    }
}
