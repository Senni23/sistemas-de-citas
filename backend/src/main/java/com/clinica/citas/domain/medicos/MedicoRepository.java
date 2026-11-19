package com.clinica.citas.domain.medicos;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de medicos. */
public interface MedicoRepository {

    Medico guardar(Medico medico);

    Optional<Medico> buscarPorId(MedicoId id);

    Optional<Medico> buscarPorCorreo(String correo);

    List<Medico> listar();

    List<Medico> listarPorIds(Collection<MedicoId> ids);

    void eliminar(MedicoId id);

    long contar();
}
