package com.clinica.citas.infrastructure.persistence.springdata;

import com.clinica.citas.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByUsername(String username);

    Optional<UsuarioJpaEntity> findByResetToken(String resetToken);

    void deleteByMedicoId(Long medicoId);

    void deleteByPacienteId(Long pacienteId);
}
