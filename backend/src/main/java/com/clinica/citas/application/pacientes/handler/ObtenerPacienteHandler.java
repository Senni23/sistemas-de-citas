package com.clinica.citas.application.pacientes.handler;

import com.clinica.citas.application.pacientes.query.PacienteQueries;
import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerPacienteHandler implements QueryHandler<PacienteQueries.PorId, PacienteView> {

    private final PacienteRepository pacientes;

    public ObtenerPacienteHandler(PacienteRepository pacientes) {
        this.pacientes = pacientes;
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteView handle(PacienteQueries.PorId query) {
        return PacienteView.de(RecursoNoEncontradoException.requerir(
                pacientes.buscarPorId(new PacienteId(query.id())), "Paciente no encontrado: " + query.id()));
    }
}
