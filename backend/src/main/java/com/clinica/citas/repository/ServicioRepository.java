package com.clinica.citas.repository;

import com.clinica.citas.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByEspecialidadId(Long especialidadId);

    Optional<Servicio> findByNombreAndEspecialidadId(String nombre, Long especialidadId);
}
