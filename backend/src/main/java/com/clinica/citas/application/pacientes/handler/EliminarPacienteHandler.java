package com.clinica.citas.application.pacientes.handler;

import com.clinica.citas.application.pacientes.command.PacienteCommands;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: eliminar un paciente junto con su cuenta de acceso (si la tiene). */
@Service
public class EliminarPacienteHandler implements CommandHandler<PacienteCommands.Eliminar, Void> {

    private final PacienteRepository pacientes;
    private final UsuarioRepository usuarios;

    public EliminarPacienteHandler(PacienteRepository pacientes, UsuarioRepository usuarios) {
        this.pacientes = pacientes;
        this.usuarios = usuarios;
    }

    @Override
    @Transactional
    public Void handle(PacienteCommands.Eliminar c) {
        PacienteId id = new PacienteId(c.id());
        RecursoNoEncontradoException.requerir(pacientes.buscarPorId(id), "Paciente no encontrado: " + c.id());
        usuarios.eliminarPorPaciente(id);
        pacientes.eliminar(id);
        return null;
    }
}
