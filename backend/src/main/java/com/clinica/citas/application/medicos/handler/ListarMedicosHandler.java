package com.clinica.citas.application.medicos.handler;

import com.clinica.citas.application.medicos.query.MedicoQueries;
import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.application.medicos.view.MedicoViewFactory;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.medicos.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarMedicosHandler implements QueryHandler<MedicoQueries.ListarTodos, List<MedicoView>> {

    private final MedicoRepository medicos;
    private final MedicoViewFactory vistas;

    public ListarMedicosHandler(MedicoRepository medicos, MedicoViewFactory vistas) {
        this.medicos = medicos;
        this.vistas = vistas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoView> handle(MedicoQueries.ListarTodos query) {
        return vistas.construirLista(medicos.listar());
    }
}
