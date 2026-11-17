package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.shared.DomainException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Objeto de valor: la fecha y la hora de una cita. Aqui vive la regla del horario de atencion
 * de la clinica: de 8:00 a 14:00 y de 16:00 a 20:00.
 */
public final class HorarioCita {

    private static final LocalTime MANANA_INICIO = LocalTime.of(8, 0);
    private static final LocalTime MANANA_FIN = LocalTime.of(14, 0);
    private static final LocalTime TARDE_INICIO = LocalTime.of(16, 0);
    private static final LocalTime TARDE_FIN = LocalTime.of(20, 0);

    private final LocalDate fecha;
    private final LocalTime hora;

    private HorarioCita(LocalDate fecha, LocalTime hora) {
        this.fecha = fecha;
        this.hora = hora;
    }

    /** Programa un horario nuevo: la hora debe caer dentro de alguno de los dos turnos. */
    public static HorarioCita programar(LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            throw new DomainException("La fecha y la hora de la cita son obligatorias");
        }
        if (!estaEnHorarioDeAtencion(hora)) {
            throw new HorarioInvalidoException("El horario de atencion es de 8:00 am a 2:00 pm y de 4:00 pm a 8:00 pm");
        }
        return new HorarioCita(fecha, hora);
    }

    /** Reconstruye desde la base de datos sin validar el turno (citas guardadas con reglas antiguas). */
    public static HorarioCita reconstruir(LocalDate fecha, LocalTime hora) {
        return new HorarioCita(fecha, hora);
    }

    public static boolean estaEnHorarioDeAtencion(LocalTime hora) {
        boolean turnoManana = !hora.isBefore(MANANA_INICIO) && !hora.isAfter(MANANA_FIN);
        boolean turnoTarde = !hora.isBefore(TARDE_INICIO) && !hora.isAfter(TARDE_FIN);
        return turnoManana || turnoTarde;
    }

    public LocalDate fecha() {
        return fecha;
    }

    public LocalTime hora() {
        return hora;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof HorarioCita h && Objects.equals(fecha, h.fecha) && Objects.equals(hora, h.hora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, hora);
    }
}
