package com.clinica.citas.application.catalogo.handler;

import com.clinica.citas.application.catalogo.query.CatalogoQueries;
import com.clinica.citas.application.catalogo.view.EspecialidadView;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarEspecialidadesHandler implements QueryHandler<CatalogoQueries.ListarEspecialidades, List<EspecialidadView>> {

    private final EspecialidadRepository especialidades;

    public ListarEspecialidadesHandler(EspecialidadRepository especialidades) {
        this.especialidades = especialidades;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadView> handle(CatalogoQueries.ListarEspecialidades query) {
        return especialidades.listar().stream().map(EspecialidadView::de).toList();
    }
}
