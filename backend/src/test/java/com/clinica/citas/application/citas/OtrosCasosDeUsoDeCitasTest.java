package com.clinica.citas.application.citas;

import com.clinica.citas.application.citas.command.CitaCommands;
import com.clinica.citas.application.citas.handler.CambiarEstadoCitaHandler;
import com.clinica.citas.application.citas.handler.EliminarCitaHandler;
import com.clinica.citas.application.citas.handler.ObtenerCitaHandler;
import com.clinica.citas.application.citas.handler.PagarCitaHandler;
import com.clinica.citas.application.citas.handler.ReprogramarCitaHandler;
import com.clinica.citas.application.citas.query.CitaQueries;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.catalogo.ServicioRepository;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.DisponibilidadMedico;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.citas.HorarioOcupadoException;
import com.clinica.citas.domain.medicos.Colegiatura;
import com.clinica.citas.domain.medicos.Medico;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OtrosCasosDeUsoDeCitasTest {

    private static final CitaId CITA_ID = new CitaId(9L);
    private static final PacienteId PACIENTE = new PacienteId(1L);
    private static final MedicoId MEDICO = new MedicoId(2L);
    private static final ServicioId SERVICIO = new ServicioId(3L);

    @Mock
    private CitaRepository citas;
    @Mock
    private PacienteRepository pacientes;
    @Mock
    private MedicoRepository medicos;
    @Mock
    private ServicioRepository servicios;
    @Mock
    private CitaViewFactory vistas;

    private Cita citaExistente;

    @BeforeEach
    void preparar() {
        citaExistente = Cita.reconstruir(CITA_ID, PACIENTE, MEDICO, SERVICIO,
                HorarioCita.reconstruir(LocalDate.of(2026, 12, 15), LocalTime.of(10, 0)), "Control",
                EstadoCita.PENDIENTE, Dinero.de("50"), null);
        when(citas.buscarPorId(CITA_ID)).thenReturn(Optional.of(citaExistente));
        when(citas.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(pacientes.buscarPorId(PACIENTE)).thenReturn(Optional.of(Paciente.reconstruir(PACIENTE,
                new NombrePersona("Ana", "Lopez"), Dni.reconstruir("1"), null, null, null, null)));
        when(medicos.buscarPorId(MEDICO)).thenReturn(Optional.of(Medico.reconstruir(MEDICO,
                new NombrePersona("Juan", "Perez"), Colegiatura.reconstruir("CMP 1"), null, null, new EspecialidadId(1L))));
    }

    @Test
    @DisplayName("Pagar marca la cita como pagada con la fecha del reloj, registra el metodo y la confirma")
    void pagar_marcaPagadaYConfirmada() {
        Clock reloj = Clock.fixed(Instant.parse("2026-10-20T15:00:00Z"), ZoneId.of("UTC"));
        when(servicios.buscarPorId(SERVICIO)).thenReturn(Optional.of(Servicio.reconstruir(SERVICIO, "Consulta",
                Dinero.de("80"), null, new EspecialidadId(1L))));

        new PagarCitaHandler(citas, servicios, reloj, vistas).handle(new CitaCommands.Pagar(9L, "TARJETA_SIMULADA"));

        ArgumentCaptor<Cita> captor = ArgumentCaptor.forClass(Cita.class);
        verify(citas).guardar(captor.capture());
        Cita pagada = captor.getValue();
        assertThat(pagada.estaPagada()).isTrue();
        assertThat(pagada.pago().metodo()).isEqualTo("TARJETA_SIMULADA");
        assertThat(pagada.pago().fecha()).isEqualTo(LocalDateTime.of(2026, 10, 20, 15, 0));
        assertThat(pagada.monto().valor()).isEqualByComparingTo("80.00");
        assertThat(pagada.estado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    @DisplayName("Cambiar el estado de una cita lo persiste")
    void cambiarEstado_actualizaYGuarda() {
        new CambiarEstadoCitaHandler(citas, vistas).handle(new CitaCommands.CambiarEstado(9L, EstadoCita.ATENDIDA));

        verify(citas).guardar(citaExistente);
        assertThat(citaExistente.estado()).isEqualTo(EstadoCita.ATENDIDA);
    }

    @Test
    @DisplayName("Reprogramar a otro horario libre actualiza la cita y excluye a la propia cita del cruce")
    void reprogramar_aHorarioLibre_actualiza() {
        when(citas.existeCitaActiva(any(), any(), any())).thenReturn(false);
        ReprogramarCitaHandler handler = new ReprogramarCitaHandler(citas, pacientes, medicos, new DisponibilidadMedico(citas), vistas);

        handler.handle(new CitaCommands.Reprogramar(9L, 1L, 2L, LocalDate.of(2026, 12, 20), LocalTime.of(17, 0), "Nuevo motivo"));

        verify(citas).existeCitaActiva(MEDICO, HorarioCita.programar(LocalDate.of(2026, 12, 20), LocalTime.of(17, 0)), CITA_ID);
        assertThat(citaExistente.horario().hora()).isEqualTo(LocalTime.of(17, 0));
        assertThat(citaExistente.motivo()).isEqualTo("Nuevo motivo");
    }

    @Test
    @DisplayName("Reprogramar a una hora ocupada por otra cita del medico es rechazado")
    void reprogramar_conCruce_lanzaHorarioOcupado() {
        when(citas.existeCitaActiva(any(), any(), any())).thenReturn(true);
        ReprogramarCitaHandler handler = new ReprogramarCitaHandler(citas, pacientes, medicos, new DisponibilidadMedico(citas), vistas);

        assertThatThrownBy(() -> handler.handle(new CitaCommands.Reprogramar(9L, 1L, 2L, LocalDate.of(2026, 12, 20), LocalTime.of(17, 0), null)))
                .isInstanceOf(HorarioOcupadoException.class);
        verify(citas, never()).guardar(any());
    }

    @Test
    @DisplayName("Buscar o eliminar una cita que no existe lanza RecursoNoEncontradoException")
    void citaInexistente_lanzaNoEncontrado() {
        when(citas.buscarPorId(new CitaId(404L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new ObtenerCitaHandler(citas, vistas).handle(new CitaQueries.PorId(404L)))
                .isInstanceOf(RecursoNoEncontradoException.class).hasMessage("Cita no encontrada: 404");
        assertThatThrownBy(() -> new EliminarCitaHandler(citas).handle(new CitaCommands.Eliminar(404L)))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(citas, never()).eliminar(any());
    }

    @Test
    @DisplayName("Eliminar borra la cita existente")
    void eliminar_borraLaCita() {
        new EliminarCitaHandler(citas).handle(new CitaCommands.Eliminar(9L));

        verify(citas).eliminar(CITA_ID);
    }
}
