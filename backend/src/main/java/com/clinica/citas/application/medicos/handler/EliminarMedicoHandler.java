package com.clinica.citas.application.medicos.handler;

import com.clinica.citas.application.medicos.command.MedicoCommands;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: eliminar un medico junto con su cuenta de acceso. */
@Service
public class EliminarMedicoHandler implements CommandHandler<MedicoCommands.Eliminar, Void> {

    private final MedicoRepository medicos;
    private final UsuarioRepository usuarios;

    public EliminarMedicoHandler(MedicoRepository medicos, UsuarioRepository usuarios) {
        this.medicos = medicos;
        this.usuarios = usuarios;
    }

    @Override
    @Transactional
    public Void handle(MedicoCommands.Eliminar c) {
        MedicoId id = new MedicoId(c.id());
        RecursoNoEncontradoException.requerir(medicos.buscarPorId(id), "Medico no encontrado: " + c.id());
        usuarios.eliminarPorMedico(id);
        medicos.eliminar(id);
        return null;
    }
}
