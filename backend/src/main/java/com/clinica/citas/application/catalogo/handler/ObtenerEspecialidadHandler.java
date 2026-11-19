package com.clinica.citas.application.catalogo.handler;

import com.clinica.citas.application.catalogo.query.CatalogoQueries;
import com.clinica.citas.application.catalogo.view.EspecialidadView;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerEspecialidadHandler implements QueryHandler<CatalogoQueries.ObtenerEspecialidad, EspecialidadView> {

    private final EspecialidadRepository especialidades;

    public ObtenerEspecialidadHandler(EspecialidadRepository especialidades) {
        this.especialidades = especialidades;
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadView handle(CatalogoQueries.ObtenerEspecialidad query) {
        return EspecialidadView.de(RecursoNoEncontradoException.requerir(
                especialidades.buscarPorId(new EspecialidadId(query.id())), "Especialidad no encontrada: " + query.id()));
    }
}
