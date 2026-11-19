package com.clinica.citas.application.dashboard.handler;

import com.clinica.citas.application.dashboard.port.ReportesDeCitas;
import com.clinica.citas.application.dashboard.query.DashboardQueries;
import com.clinica.citas.application.dashboard.view.ResumenDashboardView;
import com.clinica.citas.application.dashboard.view.ResumenDashboardView.CitasPorEspecialidad;
import com.clinica.citas.application.dashboard.view.ResumenDashboardView.CitasPorEstado;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

/** Consulta: los totales y reportes que se muestran en el panel de administracion. */
@Service
public class ObtenerResumenDashboardHandler implements QueryHandler<DashboardQueries.ObtenerResumen, ResumenDashboardView> {

    private final PacienteRepository pacientes;
    private final MedicoRepository medicos;
    private final CitaRepository citas;
    private final ReportesDeCitas reportes;
    private final Clock reloj;

    public ObtenerResumenDashboardHandler(PacienteRepository pacientes, MedicoRepository medicos, CitaRepository citas,
                                          ReportesDeCitas reportes, Clock reloj) {
        this.pacientes = pacientes;
        this.medicos = medicos;
        this.citas = citas;
        this.reportes = reportes;
        this.reloj = reloj;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenDashboardView handle(DashboardQueries.ObtenerResumen query) {
        return new ResumenDashboardView(
                pacientes.contar(),
                medicos.contar(),
                citas.contar(),
                citas.contarPorFecha(LocalDate.now(reloj)),
                reportes.citasPorEstado().stream()
                        .map(c -> new CitasPorEstado(c.estado().name(), c.total())).toList(),
                reportes.citasActivasPorEspecialidad().stream()
                        .map(c -> new CitasPorEspecialidad(c.especialidad(), c.total())).toList());
    }
}
