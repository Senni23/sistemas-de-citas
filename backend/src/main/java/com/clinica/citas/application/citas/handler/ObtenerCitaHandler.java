package com.clinica.citas.application.citas.handler;

import com.clinica.citas.application.citas.query.CitaQueries;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerCitaHandler implements QueryHandler<CitaQueries.PorId, CitaView> {

    private final CitaRepository citas;
    private final CitaViewFactory vistas;

    public ObtenerCitaHandler(CitaRepository citas, CitaViewFactory vistas) {
        this.citas = citas;
        this.vistas = vistas;
    }

    @Override
    @Transactional(readOnly = true)
    public CitaView handle(CitaQueries.PorId query) {
        return vistas.construir(RecursoNoEncontradoException.requerir(
                citas.buscarPorId(new CitaId(query.id())), "Cita no encontrada: " + query.id()));
    }
}
