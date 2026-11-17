package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.shared.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HorarioCitaTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 12, 15);

    @ParameterizedTest
    @ValueSource(strings = {"08:00", "10:30", "14:00", "16:00", "18:45", "20:00"})
    @DisplayName("Las horas dentro de los dos turnos (8-14 y 16-20), incluidos sus limites, son validas")
    void horasDentroDeLosTurnos_sonValidas(String hora) {
        HorarioCita horario = HorarioCita.programar(FECHA, LocalTime.parse(hora));

        assertThat(horario.fecha()).isEqualTo(FECHA);
        assertThat(horario.hora()).isEqualTo(LocalTime.parse(hora));
    }

    @ParameterizedTest
    @ValueSource(strings = {"07:59", "14:01", "15:00", "15:59", "20:01", "23:00"})
    @DisplayName("Una hora fuera de los turnos de atencion es rechazada")
    void horasFueraDeLosTurnos_sonRechazadas(String hora) {
        assertThatThrownBy(() -> HorarioCita.programar(FECHA, LocalTime.parse(hora)))
                .isInstanceOf(HorarioInvalidoException.class)
                .hasMessageContaining("horario de atencion");
    }

    @Test
    @DisplayName("La fecha y la hora son obligatorias")
    void sinFechaOHora_lanzaError() {
        assertThatThrownBy(() -> HorarioCita.programar(null, LocalTime.of(9, 0))).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> HorarioCita.programar(FECHA, null)).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Reconstruir desde la base de datos no vuelve a validar el turno (citas antiguas)")
    void reconstruir_noValidaElTurno() {
        HorarioCita antiguo = HorarioCita.reconstruir(FECHA, LocalTime.of(22, 0));

        assertThat(antiguo.hora()).isEqualTo(LocalTime.of(22, 0));
    }

    @Test
    @DisplayName("Dos horarios con la misma fecha y hora son iguales (objeto de valor)")
    void igualdadPorValor() {
        assertThat(HorarioCita.programar(FECHA, LocalTime.of(9, 0)))
                .isEqualTo(HorarioCita.programar(FECHA, LocalTime.of(9, 0)));
    }
}
