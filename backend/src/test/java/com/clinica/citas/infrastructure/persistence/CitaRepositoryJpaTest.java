package com.clinica.citas.infrastructure.persistence;

import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.infrastructure.persistence.mapper.CitaMapper;
import com.clinica.citas.infrastructure.persistence.repository.CitaRepositoryJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Prueba la implementacion real del repositorio (Spring Data + mapeador) contra una base H2 en memoria. */
@DataJpaTest
@ActiveProfiles("test")
@Import({CitaRepositoryJpa.class, CitaMapper.class})
class CitaRepositoryJpaTest {

    private static final MedicoId MEDICO = new MedicoId(2L);
    private static final HorarioCita HORARIO = HorarioCita.programar(LocalDate.of(2027, 1, 10), LocalTime.of(10, 0));

    @Autowired
    private CitaRepositoryJpa repositorio;

    private Cita guardarCita(MedicoId medico, HorarioCita horario) {
        return repositorio.guardar(Cita.agendar(new PacienteId(1L), medico, new ServicioId(3L), Dinero.de("50"), horario, "Control"));
    }

    @Test
    @DisplayName("Guardar y volver a buscar una cita conserva todos sus datos")
    void guardarYBuscar_conservaLosDatos() {
        Cita guardada = guardarCita(MEDICO, HORARIO);

        Cita leida = repositorio.buscarPorId(guardada.id()).orElseThrow();

        assertThat(leida.id()).isNotNull();
        assertThat(leida.pacienteId()).isEqualTo(new PacienteId(1L));
        assertThat(leida.medicoId()).isEqualTo(MEDICO);
        assertThat(leida.servicioId()).isEqualTo(new ServicioId(3L));
        assertThat(leida.horario()).isEqualTo(HORARIO);
        assertThat(leida.estado()).isEqualTo(EstadoCita.PENDIENTE);
        assertThat(leida.monto().valor()).isEqualByComparingTo("50.00");
        assertThat(leida.estaPagada()).isFalse();
    }

    @Test
    @DisplayName("El pago se guarda y se recupera (metodo, fecha y estado CONFIRMADA)")
    void elPagoSePersiste() {
        Cita cita = guardarCita(MEDICO, HORARIO);
        cita.pagar("TARJETA_SIMULADA", LocalDateTime.of(2026, 10, 20, 12, 0), Dinero.de("50"));
        repositorio.guardar(cita);

        Cita leida = repositorio.buscarPorId(cita.id()).orElseThrow();

        assertThat(leida.estaPagada()).isTrue();
        assertThat(leida.pago().metodo()).isEqualTo("TARJETA_SIMULADA");
        assertThat(leida.pago().fecha()).isEqualTo(LocalDateTime.of(2026, 10, 20, 12, 0));
        assertThat(leida.estado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    @DisplayName("Una cita activa del mismo medico en la misma fecha y hora cuenta como cruce")
    void citaActiva_bloqueaElHorario() {
        guardarCita(MEDICO, HORARIO);

        assertThat(repositorio.existeCitaActiva(MEDICO, HORARIO, null)).isTrue();
        assertThat(repositorio.existeCitaActiva(new MedicoId(99L), HORARIO, null)).isFalse();
    }

    @Test
    @DisplayName("Las citas canceladas no bloquean el horario")
    void citaCancelada_noBloquea() {
        Cita cita = guardarCita(MEDICO, HORARIO);
        cita.cambiarEstado(EstadoCita.CANCELADA);
        repositorio.guardar(cita);

        assertThat(repositorio.existeCitaActiva(MEDICO, HORARIO, null)).isFalse();
    }

    @Test
    @DisplayName("Al reprogramar, la propia cita no cuenta como cruce consigo misma")
    void laPropiaCita_noCuentaComoCruce() {
        Cita cita = guardarCita(MEDICO, HORARIO);

        assertThat(repositorio.existeCitaActiva(MEDICO, HORARIO, cita.id())).isFalse();
        assertThat(repositorio.existeCitaActiva(MEDICO, HORARIO, new CitaId(cita.id().valor() + 1000))).isTrue();
    }

    @Test
    @DisplayName("Se pueden contar las citas del dia y listar las de un paciente o de un medico")
    void contarYListar() {
        guardarCita(MEDICO, HORARIO);
        guardarCita(new MedicoId(3L), HorarioCita.programar(LocalDate.of(2027, 1, 10), LocalTime.of(11, 0)));

        assertThat(repositorio.contarPorFecha(LocalDate.of(2027, 1, 10))).isEqualTo(2);
        assertThat(repositorio.listarPorMedico(MEDICO)).hasSize(1);
        assertThat(repositorio.listarPorPaciente(new PacienteId(1L))).hasSize(2);
    }
}
