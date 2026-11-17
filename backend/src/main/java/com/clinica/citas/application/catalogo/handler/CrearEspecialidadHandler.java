package com.clinica.citas.application.catalogo.handler;

import com.clinica.citas.application.catalogo.command.EspecialidadCommands;
import com.clinica.citas.application.catalogo.view.EspecialidadView;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearEspecialidadHandler implements CommandHandler<EspecialidadCommands.Crear, EspecialidadView> {

    private final EspecialidadRepository especialidades;

    public CrearEspecialidadHandler(EspecialidadRepository especialidades) {
        this.especialidades = especialidades;
    }

    @Override
    @Transactional
    public EspecialidadView handle(EspecialidadCommands.Crear c) {
        Especialidad nueva = Especialidad.crear(c.nombre(), c.descripcion());
        if (especialidades.buscarPorNombre(nueva.nombre()).isPresent()) {
            throw new RegistroDuplicadoException("Ya existe una especialidad con ese nombre");
        }
        return EspecialidadView.de(especialidades.guardar(nueva));
    }
}
