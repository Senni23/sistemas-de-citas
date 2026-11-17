package com.clinica.citas.infrastructure.persistence.springdata;

import com.clinica.citas.infrastructure.persistence.entity.ServicioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicioJpaRepository extends JpaRepository<ServicioJpaEntity, Long> {

    List<ServicioJpaEntity> findByEspecialidadId(Long especialidadId);

    Optional<ServicioJpaEntity> findByNombreAndEspecialidadId(String nombre, Long especialidadId);
}
