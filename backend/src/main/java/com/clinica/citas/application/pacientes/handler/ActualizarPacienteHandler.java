package com.clinica.citas.application.pacientes.handler;

import com.clinica.citas.application.pacientes.command.PacienteCommands;
import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: actualizar los datos de un paciente. */
@Service
public class ActualizarPacienteHandler implements CommandHandler<PacienteCommands.Actualizar, PacienteView> {

    private final PacienteRepository pacientes;

    public ActualizarPacienteHandler(PacienteRepository pacientes) {
        this.pacientes = pacientes;
    }

    @Override
    @Transactional
    public PacienteView handle(PacienteCommands.Actualizar c) {
        PacienteId id = new PacienteId(c.id());
        Paciente paciente = RecursoNoEncontradoException.requerir(pacientes.buscarPorId(id), "Paciente no encontrado: " + c.id());
        Dni dni = Dni.de(c.dni());
        pacientes.buscarPorDni(dni).filter(otro -> !otro.id().equals(id)).ifPresent(otro -> {
            throw new RegistroDuplicadoException("Ya existe un paciente registrado con ese DNI");
        });
        paciente.actualizarDatos(new NombrePersona(c.nombres(), c.apellidos()), dni, c.fechaNacimiento(),
                c.telefono(), CorreoElectronico.opcional(c.email()), c.direccion());
        return PacienteView.de(pacientes.guardar(paciente));
    }
}
