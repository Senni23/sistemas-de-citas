package com.clinica.citas.infrastructure.persistence.springdata;

import com.clinica.citas.infrastructure.persistence.entity.MedicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicoJpaRepository extends JpaRepository<MedicoJpaEntity, Long> {

    Optional<MedicoJpaEntity> findByEmail(String email);
}
