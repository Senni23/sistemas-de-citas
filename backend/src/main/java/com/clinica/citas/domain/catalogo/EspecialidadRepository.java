package com.clinica.citas.domain.catalogo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de especialidades. */
public interface EspecialidadRepository {

    Especialidad guardar(Especialidad especialidad);

    Optional<Especialidad> buscarPorId(EspecialidadId id);

    Optional<Especialidad> buscarPorNombre(String nombre);

    List<Especialidad> listar();

    List<Especialidad> listarPorIds(Collection<EspecialidadId> ids);

    void eliminar(EspecialidadId id);
}
