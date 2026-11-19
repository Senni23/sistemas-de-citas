package com.clinica.citas.application.citas.handler;

import com.clinica.citas.application.citas.command.CitaCommands;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarCitaHandler implements CommandHandler<CitaCommands.Eliminar, Void> {

    private final CitaRepository citas;

    public EliminarCitaHandler(CitaRepository citas) {
        this.citas = citas;
    }

    @Override
    @Transactional
    public Void handle(CitaCommands.Eliminar c) {
        CitaId id = new CitaId(c.id());
        RecursoNoEncontradoException.requerir(citas.buscarPorId(id), "Cita no encontrada: " + c.id());
        citas.eliminar(id);
        return null;
    }
}
