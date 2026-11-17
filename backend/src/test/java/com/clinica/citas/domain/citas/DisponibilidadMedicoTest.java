package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.medicos.MedicoId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisponibilidadMedicoTest {

    private static final MedicoId MEDICO = new MedicoId(2L);
    private static final HorarioCita HORARIO = HorarioCita.programar(LocalDate.of(2026, 12, 15), LocalTime.of(10, 0));

    @Mock
    private CitaRepository citas;

    @Test
    @DisplayName("Si el medico ya tiene una cita activa en ese horario, se rechaza con HorarioOcupadoException")
    void medicoOcupado_lanzaHorarioOcupado() {
        when(citas.existeCitaActiva(MEDICO, HORARIO, null)).thenReturn(true);

        assertThatThrownBy(() -> new DisponibilidadMedico(citas).exigirLibre(MEDICO, HORARIO, null))
                .isInstanceOf(HorarioOcupadoException.class)
                .hasMessage("El medico ya tiene una cita en esa fecha y hora");
    }

    @Test
    @DisplayName("Si el medico esta libre, no pasa nada")
    void medicoLibre_noLanzaError() {
        when(citas.existeCitaActiva(MEDICO, HORARIO, null)).thenReturn(false);

        assertThatCode(() -> new DisponibilidadMedico(citas).exigirLibre(MEDICO, HORARIO, null)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Al reprogramar se le indica al repositorio que la propia cita no cuenta como cruce")
    void alReprogramar_excluyeLaPropiaCita() {
        CitaId propia = new CitaId(9L);
        when(citas.existeCitaActiva(MEDICO, HORARIO, propia)).thenReturn(false);

        new DisponibilidadMedico(citas).exigirLibre(MEDICO, HORARIO, propia);

        verify(citas).existeCitaActiva(MEDICO, HORARIO, propia);
    }
}
