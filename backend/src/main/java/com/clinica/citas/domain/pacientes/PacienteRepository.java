package com.clinica.citas.domain.pacientes;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de pacientes. */
public interface PacienteRepository {

    Paciente guardar(Paciente paciente);

    Optional<Paciente> buscarPorId(PacienteId id);

    Optional<Paciente> buscarPorDni(Dni dni);

    List<Paciente> listar();

    List<Paciente> listarPorIds(Collection<PacienteId> ids);

    void eliminar(PacienteId id);

    long contar();
}
