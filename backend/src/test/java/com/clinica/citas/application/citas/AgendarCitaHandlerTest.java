package com.clinica.citas.application.citas;

import com.clinica.citas.application.citas.command.CitaCommands;
import com.clinica.citas.application.citas.handler.AgendarCitaHandler;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.catalogo.ServicioRepository;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.DisponibilidadMedico;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.citas.HorarioInvalidoException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AgendarCitaHandlerTest {

    private static final PacienteId PACIENTE = new PacienteId(1L);
    private static final MedicoId MEDICO = new MedicoId(2L);
    private static final ServicioId SERVICIO = new ServicioId(3L);
    private static final LocalDate FECHA = LocalDate.of(2026, 12, 15);

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

    private AgendarCitaHandler handler;

    @BeforeEach
    void preparar() {
        handler = new AgendarCitaHandler(citas, pacientes, medicos, servicios, new DisponibilidadMedico(citas), vistas);
        when(pacientes.buscarPorId(PACIENTE)).thenReturn(Optional.of(Paciente.reconstruir(PACIENTE,
                new NombrePersona("Ana", "Lopez"), Dni.reconstruir("11111111"), null, null, null, null)));
        when(medicos.buscarPorId(MEDICO)).thenReturn(Optional.of(Medico.reconstruir(MEDICO,
                new NombrePersona("Juan", "Perez"), Colegiatura.reconstruir("CMP 1"), null, null, new EspecialidadId(1L))));
        when(servicios.buscarPorId(SERVICIO)).thenReturn(Optional.of(Servicio.reconstruir(SERVICIO, "Hemograma",
                Dinero.de("50"), null, new EspecialidadId(1L))));
        when(citas.existeCitaActiva(any(), any(), isNull())).thenReturn(false);
        when(citas.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    private CitaCommands.Agendar comando(String hora, Long servicioId) {
        return new CitaCommands.Agendar(1L, 2L, servicioId, FECHA, LocalTime.parse(hora), "Control");
    }

    @Test
    @DisplayName("Una cita en turno valido se guarda PENDIENTE, sin pagar y con el precio del servicio como monto")
    void agendar_guardaPendienteSinPagarConElPrecioDelServicio() {
        handler.handle(comando("10:00", 3L));

        ArgumentCaptor<Cita> captor = ArgumentCaptor.forClass(Cita.class);
        verify(citas).guardar(captor.capture());
        Cita guardada = captor.getValue();
        assertThat(guardada.estado()).isEqualTo(EstadoCita.PENDIENTE);
        assertThat(guardada.estaPagada()).isFalse();
        assertThat(guardada.servicioId()).isEqualTo(SERVICIO);
        assertThat(guardada.monto().valor()).isEqualByComparingTo("50.00");
        assertThat(guardada.horario()).isEqualTo(HorarioCita.programar(FECHA, LocalTime.of(10, 0)));
    }

    @Test
    @DisplayName("Una cita sin servicio se guarda sin monto")
    void agendar_sinServicio_noTieneMonto() {
        handler.handle(comando("10:00", null));

        ArgumentCaptor<Cita> captor = ArgumentCaptor.forClass(Cita.class);
        verify(citas).guardar(captor.capture());
        assertThat(captor.getValue().servicioId()).isNull();
        assertThat(captor.getValue().monto()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"08:00", "14:00", "16:00", "20:00"})
    @DisplayName("Los limites de cada turno son horas validas")
    void agendar_enLosLimitesDeTurno_esAceptada(String hora) {
        handler.handle(comando(hora, null));

        verify(citas).guardar(any(Cita.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"07:59", "14:01", "15:00", "20:01"})
    @DisplayName("Una hora fuera de los turnos es rechazada y no se guarda nada")
    void agendar_fueraDeTurno_lanzaHorarioInvalido(String hora) {
        assertThatThrownBy(() -> handler.handle(comando(hora, null))).isInstanceOf(HorarioInvalidoException.class);

        verify(citas, never()).guardar(any());
    }

    @Test
    @DisplayName("No se puede agendar con un medico que ya tiene una cita activa en esa fecha y hora")
    void agendar_medicoOcupado_lanzaHorarioOcupado() {
        when(citas.existeCitaActiva(eq(MEDICO), eq(HorarioCita.programar(FECHA, LocalTime.of(10, 0))), isNull())).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(comando("10:00", null)))
                .isInstanceOf(HorarioOcupadoException.class)
                .hasMessage("El medico ya tiene una cita en esa fecha y hora");
        verify(citas, never()).guardar(any());
    }

    @Test
    @DisplayName("Si el paciente, el medico o el servicio no existen se responde 'no encontrado'")
    void agendar_conReferenciasInexistentes_lanzaNoEncontrado() {
        when(pacientes.buscarPorId(PACIENTE)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> handler.handle(comando("10:00", null)))
                .isInstanceOf(RecursoNoEncontradoException.class).hasMessage("Paciente no encontrado: 1");

        when(pacientes.buscarPorId(PACIENTE)).thenReturn(Optional.of(Paciente.reconstruir(PACIENTE,
                new NombrePersona("Ana", "Lopez"), Dni.reconstruir("1"), null, null, null, null)));
        when(medicos.buscarPorId(MEDICO)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> handler.handle(comando("10:00", null)))
                .isInstanceOf(RecursoNoEncontradoException.class).hasMessage("Medico no encontrado: 2");

        verify(citas, never()).guardar(any());
    }
}
