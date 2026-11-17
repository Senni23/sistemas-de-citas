package com.clinica.citas.application.pacientes.handler;

import com.clinica.citas.application.pacientes.query.PacienteQueries;
import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarPacientesHandler implements QueryHandler<PacienteQueries.ListarTodos, List<PacienteView>> {

    private final PacienteRepository pacientes;

    public ListarPacientesHandler(PacienteRepository pacientes) {
        this.pacientes = pacientes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteView> handle(PacienteQueries.ListarTodos query) {
        return pacientes.listar().stream().map(PacienteView::de).toList();
    }
}
