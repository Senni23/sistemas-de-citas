package com.clinica.citas.infrastructure.persistence.springdata;

import com.clinica.citas.infrastructure.persistence.entity.EspecialidadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EspecialidadJpaRepository extends JpaRepository<EspecialidadJpaEntity, Long> {

    Optional<EspecialidadJpaEntity> findByNombre(String nombre);
}
