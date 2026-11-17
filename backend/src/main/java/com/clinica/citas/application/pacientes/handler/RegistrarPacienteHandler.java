package com.clinica.citas.application.pacientes.handler;

import com.clinica.citas.application.pacientes.command.PacienteCommands;
import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: registrar un paciente (no se permiten dos pacientes con el mismo DNI). */
@Service
public class RegistrarPacienteHandler implements CommandHandler<PacienteCommands.Registrar, PacienteView> {

    private final PacienteRepository pacientes;

    public RegistrarPacienteHandler(PacienteRepository pacientes) {
        this.pacientes = pacientes;
    }

    @Override
    @Transactional
    public PacienteView handle(PacienteCommands.Registrar c) {
        Dni dni = Dni.de(c.dni());
        if (pacientes.buscarPorDni(dni).isPresent()) {
            throw new RegistroDuplicadoException("Ya existe un paciente registrado con ese DNI");
        }
        Paciente paciente = Paciente.registrar(new NombrePersona(c.nombres(), c.apellidos()), dni, c.fechaNacimiento(),
                c.telefono(), CorreoElectronico.opcional(c.email()), c.direccion());
        return PacienteView.de(pacientes.guardar(paciente));
    }
}
