package com.clinica.citas.application.dashboard;

import com.clinica.citas.application.dashboard.handler.ObtenerResumenDashboardHandler;
import com.clinica.citas.application.dashboard.port.ReportesDeCitas;
import com.clinica.citas.application.dashboard.port.ReportesDeCitas.ConteoPorEspecialidad;
import com.clinica.citas.application.dashboard.port.ReportesDeCitas.ConteoPorEstado;
import com.clinica.citas.application.dashboard.query.DashboardQueries;
import com.clinica.citas.application.dashboard.view.ResumenDashboardView;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerResumenDashboardHandlerTest {

    @Mock
    private PacienteRepository pacientes;
    @Mock
    private MedicoRepository medicos;
    @Mock
    private CitaRepository citas;
    @Mock
    private ReportesDeCitas reportes;

    @Test
    @DisplayName("El resumen junta los totales, las citas de hoy y los dos reportes")
    void armaElResumen() {
        Clock reloj = Clock.fixed(Instant.parse("2026-12-15T15:00:00Z"), ZoneId.of("America/Lima"));
        when(pacientes.contar()).thenReturn(32L);
        when(medicos.contar()).thenReturn(9L);
        when(citas.contar()).thenReturn(14L);
        when(citas.contarPorFecha(LocalDate.of(2026, 12, 15))).thenReturn(3L);
        when(reportes.citasPorEstado()).thenReturn(List.of(
                new ConteoPorEstado(EstadoCita.PENDIENTE, 10), new ConteoPorEstado(EstadoCita.CANCELADA, 4)));
        when(reportes.citasActivasPorEspecialidad()).thenReturn(List.of(new ConteoPorEspecialidad("Cardiologia", 7)));

        ResumenDashboardView resumen = new ObtenerResumenDashboardHandler(pacientes, medicos, citas, reportes, reloj)
                .handle(new DashboardQueries.ObtenerResumen());

        assertThat(resumen.totalPacientes()).isEqualTo(32);
        assertThat(resumen.totalMedicos()).isEqualTo(9);
        assertThat(resumen.totalCitas()).isEqualTo(14);
        assertThat(resumen.citasHoy()).isEqualTo(3);
        assertThat(resumen.citasPorEstado()).containsExactly(
                new ResumenDashboardView.CitasPorEstado("PENDIENTE", 10),
                new ResumenDashboardView.CitasPorEstado("CANCELADA", 4));
        assertThat(resumen.citasPorEspecialidad())
                .containsExactly(new ResumenDashboardView.CitasPorEspecialidad("Cardiologia", 7));
    }
}
