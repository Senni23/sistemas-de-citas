package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.Pagina;
import com.clinica.citas.domain.shared.SolicitudDePagina;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de citas. El dominio solo lo declara; la infraestructura lo implementa. */
public interface CitaRepository {

    Cita guardar(Cita cita);

    Optional<Cita> buscarPorId(CitaId id);

    List<Cita> listar();

    List<Cita> listarPorPaciente(PacienteId pacienteId);

    /** Agenda del medico, ordenada por fecha y hora. */
    List<Cita> listarPorMedico(MedicoId medicoId);

    /**
     * Busca citas de a una pagina, con orden.
     *
     * @param estado si no es null, solo trae las citas en ese estado
     */
    Pagina<Cita> buscarPagina(EstadoCita estado, OrdenDeCitas orden, boolean descendente, SolicitudDePagina solicitud);

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
