package com.clinica.citas.application.catalogo.handler;

import com.clinica.citas.application.catalogo.command.EspecialidadCommands;
import com.clinica.citas.application.catalogo.view.EspecialidadView;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarEspecialidadHandler implements CommandHandler<EspecialidadCommands.Actualizar, EspecialidadView> {

    private final EspecialidadRepository especialidades;

    public ActualizarEspecialidadHandler(EspecialidadRepository especialidades) {
        this.especialidades = especialidades;
    }

    @Override
    @Transactional
    public EspecialidadView handle(EspecialidadCommands.Actualizar c) {
        EspecialidadId id = new EspecialidadId(c.id());
        Especialidad especialidad = RecursoNoEncontradoException.requerir(
                especialidades.buscarPorId(id), "Especialidad no encontrada: " + c.id());
        especialidad.actualizar(c.nombre(), c.descripcion());
        especialidades.buscarPorNombre(especialidad.nombre()).filter(otra -> !otra.id().equals(id)).ifPresent(otra -> {
            throw new RegistroDuplicadoException("Ya existe una especialidad con ese nombre");
        });
        return EspecialidadView.de(especialidades.guardar(especialidad));
    }
}
