package com.clinica.citas.application.citas.handler;

import com.clinica.citas.application.citas.query.CitaQueries;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.medicos.MedicoId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarCitasPorMedicoHandler implements QueryHandler<CitaQueries.PorMedico, List<CitaView>> {

    private final CitaRepository citas;
    private final CitaViewFactory vistas;

    public ListarCitasPorMedicoHandler(CitaRepository citas, CitaViewFactory vistas) {
        this.citas = citas;
        this.vistas = vistas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaView> handle(CitaQueries.PorMedico query) {
        return vistas.construirLista(citas.listarPorMedico(new MedicoId(query.medicoId())));
    }
}
