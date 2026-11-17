package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CitaTest {

    private static final PacienteId PACIENTE = new PacienteId(1L);
    private static final MedicoId MEDICO = new MedicoId(2L);
    private static final ServicioId SERVICIO = new ServicioId(3L);
    private static final HorarioCita HORARIO = HorarioCita.programar(LocalDate.of(2026, 12, 15), LocalTime.of(10, 0));

    private Cita citaNueva() {
        return Cita.agendar(PACIENTE, MEDICO, SERVICIO, Dinero.de("80"), HORARIO, "Control");
    }

    @Test
    @DisplayName("Una cita agendada nace PENDIENTE, sin pagar y con el precio del servicio como monto")
    void agendar_nacePendienteSinPagarConElMontoDelServicio() {
        Cita cita = citaNueva();

        assertThat(cita.id()).isNull();
        assertThat(cita.estado()).isEqualTo(EstadoCita.PENDIENTE);
        assertThat(cita.estaPagada()).isFalse();
        assertThat(cita.monto().valor()).isEqualByComparingTo("80.00");
        assertThat(cita.servicioId()).isEqualTo(SERVICIO);
        assertThat(cita.estaActiva()).isTrue();
    }

    @Test
    @DisplayName("Una cita sin servicio queda sin monto")
    void agendar_sinServicio_noTieneMonto() {
        Cita cita = Cita.agendar(PACIENTE, MEDICO, null, null, HORARIO, null);

        assertThat(cita.servicioId()).isNull();
        assertThat(cita.monto()).isNull();
    }

    @Test
    @DisplayName("Agendar exige paciente, medico y horario")
    void agendar_sinParticipantes_lanzaError() {
        assertThatThrownBy(() -> Cita.agendar(null, MEDICO, null, null, HORARIO, null)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Cita.agendar(PACIENTE, null, null, null, HORARIO, null)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Cita.agendar(PACIENTE, MEDICO, null, null, null, null)).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("El motivo no puede pasar de 255 caracteres")
    void agendar_motivoMuyLargo_lanzaError() {
        String largo = "x".repeat(256);

        assertThatThrownBy(() -> Cita.agendar(PACIENTE, MEDICO, null, null, HORARIO, largo))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("255");
    }

    @Test
    @DisplayName("Pagar marca la cita como pagada, registra metodo y fecha, y la confirma")
    void pagar_marcaPagadaYConfirmada() {
        Cita cita = citaNueva();
        LocalDateTime ahora = LocalDateTime.of(2026, 10, 20, 15, 30);

        cita.pagar("TARJETA_SIMULADA", ahora, Dinero.de("90"));

        assertThat(cita.estaPagada()).isTrue();
        assertThat(cita.pago().metodo()).isEqualTo("TARJETA_SIMULADA");
        assertThat(cita.pago().fecha()).isEqualTo(ahora);
        assertThat(cita.monto().valor()).isEqualByComparingTo("90.00");
        assertThat(cita.estado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    @DisplayName("Si el servicio ya no existe al pagar, se conserva el monto que tenia la cita")
    void pagar_sinPrecio_conservaElMontoAnterior() {
        Cita cita = citaNueva();

        cita.pagar("EFECTIVO", LocalDateTime.of(2026, 10, 20, 9, 0), null);

        assertThat(cita.monto().valor()).isEqualByComparingTo("80.00");
    }

    @Test
    @DisplayName("El metodo de pago es obligatorio")
    void pagar_sinMetodo_lanzaError() {
        Cita cita = citaNueva();

        assertThatThrownBy(() -> cita.pagar(" ", LocalDateTime.now(), null)).isInstanceOf(DomainException.class);
        assertThat(cita.estaPagada()).isFalse();
    }

    @Test
    @DisplayName("Reprogramar cambia paciente, medico, horario y motivo")
    void reprogramar_cambiaLosDatos() {
        Cita cita = citaNueva();
        HorarioCita nuevo = HorarioCita.programar(LocalDate.of(2026, 12, 20), LocalTime.of(17, 0));

        cita.reprogramar(new PacienteId(7L), new MedicoId(8L), nuevo, "Otro motivo");

        assertThat(cita.pacienteId()).isEqualTo(new PacienteId(7L));
        assertThat(cita.medicoId()).isEqualTo(new MedicoId(8L));
        assertThat(cita.horario()).isEqualTo(nuevo);
        assertThat(cita.motivo()).isEqualTo("Otro motivo");
    }

    @Test
    @DisplayName("Cancelar una cita la deja inactiva; el estado nunca puede ser nulo")
    void cambiarEstado_cancelaYNoAceptaNulo() {
        Cita cita = citaNueva();

        cita.cambiarEstado(EstadoCita.CANCELADA);
        assertThat(cita.estaActiva()).isFalse();

        assertThatThrownBy(() -> cita.cambiarEstado(null)).isInstanceOf(DomainException.class);
    }
}
