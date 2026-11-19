package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.medicos.MedicoId;

/**
 * Servicio de dominio: la regla "un medico no puede tener dos citas activas a la misma hora".
 * Es un servicio (y no un metodo de Cita) porque necesita consultar a las demas citas.
 */
public class DisponibilidadMedico {

    private final CitaRepository citas;

    public DisponibilidadMedico(CitaRepository citas) {
        this.citas = citas;
    }

    public void exigirLibre(MedicoId medicoId, HorarioCita horario, CitaId citaQueSeReprograma) {
        if (citas.existeCitaActiva(medicoId, horario, citaQueSeReprograma)) {
            throw new HorarioOcupadoException("El medico ya tiene una cita en esa fecha y hora");
        }
    }
}
