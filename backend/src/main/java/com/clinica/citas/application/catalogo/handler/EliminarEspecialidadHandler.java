package com.clinica.citas.application.catalogo.handler;

import com.clinica.citas.application.catalogo.command.EspecialidadCommands;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarEspecialidadHandler implements CommandHandler<EspecialidadCommands.Eliminar, Void> {

    private final EspecialidadRepository especialidades;

    public EliminarEspecialidadHandler(EspecialidadRepository especialidades) {
        this.especialidades = especialidades;
    }

    @Override
    @Transactional
    public Void handle(EspecialidadCommands.Eliminar c) {
        EspecialidadId id = new EspecialidadId(c.id());
        RecursoNoEncontradoException.requerir(especialidades.buscarPorId(id), "Especialidad no encontrada: " + c.id());
        especialidades.eliminar(id);
        return null;
    }
}
