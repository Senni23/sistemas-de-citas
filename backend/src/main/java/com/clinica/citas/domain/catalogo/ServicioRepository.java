package com.clinica.citas.domain.catalogo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de servicios. */
public interface ServicioRepository {

    Servicio guardar(Servicio servicio);

    Optional<Servicio> buscarPorId(ServicioId id);

    Optional<Servicio> buscarPorNombreYEspecialidad(String nombre, EspecialidadId especialidadId);

    List<Servicio> listar();

    List<Servicio> listarPorEspecialidad(EspecialidadId especialidadId);

    List<Servicio> listarPorIds(Collection<ServicioId> ids);
}
