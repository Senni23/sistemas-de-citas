package com.clinica.citas.infrastructure.persistence.springdata;

import com.clinica.citas.infrastructure.persistence.entity.PacienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteJpaRepository extends JpaRepository<PacienteJpaEntity, Long> {

    Optional<PacienteJpaEntity> findByDni(String dni);
}
