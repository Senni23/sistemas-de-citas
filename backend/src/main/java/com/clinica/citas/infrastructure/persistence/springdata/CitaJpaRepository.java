package com.clinica.citas.infrastructure.persistence.springdata;

import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.infrastructure.persistence.entity.CitaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaJpaRepository extends JpaRepository<CitaJpaEntity, Long> {

    List<CitaJpaEntity> findByPacienteId(Long pacienteId);

    List<CitaJpaEntity> findByMedicoId(Long medicoId);

    long countByFecha(LocalDate fecha);

    boolean existsByMedicoIdAndFechaAndHoraAndEstadoNot(Long medicoId, LocalDate fecha, LocalTime hora, EstadoCita estado);

    boolean existsByMedicoIdAndFechaAndHoraAndEstadoNotAndIdNot(Long medicoId, LocalDate fecha, LocalTime hora,
                                                                EstadoCita estado, Long id);
}
