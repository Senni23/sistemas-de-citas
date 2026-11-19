package com.clinica.citas.application.medicos.handler;

import com.clinica.citas.application.medicos.query.MedicoQueries;
import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.application.medicos.view.MedicoViewFactory;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerMedicoHandler implements QueryHandler<MedicoQueries.PorId, MedicoView> {

    private final MedicoRepository medicos;
    private final MedicoViewFactory vistas;

    public ObtenerMedicoHandler(MedicoRepository medicos, MedicoViewFactory vistas) {
        this.medicos = medicos;
        this.vistas = vistas;
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoView handle(MedicoQueries.PorId query) {
        return vistas.construir(RecursoNoEncontradoException.requerir(
                medicos.buscarPorId(new MedicoId(query.id())), "Medico no encontrado: " + query.id()));
    }
}
