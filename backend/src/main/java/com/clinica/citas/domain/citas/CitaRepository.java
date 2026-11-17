package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de citas. El dominio solo lo declara; la infraestructura lo implementa. */
public interface CitaRepository {

    Cita guardar(Cita cita);

    Optional<Cita> buscarPorId(CitaId id);

    List<Cita> listar();

    List<Cita> listarPorPaciente(PacienteId pacienteId);

    List<Cita> listarPorMedico(MedicoId medicoId);

    void eliminar(CitaId id);

    long contar();

    long contarPorFecha(LocalDate fecha);

    /**
     * Indica si el medico ya tiene una cita activa (no cancelada) en ese horario.
     *
     * @param excluir cita que no cuenta como cruce (la que se esta reprogramando); puede ser null
     */
    boolean existeCitaActiva(MedicoId medicoId, HorarioCita horario, CitaId excluir);
}
