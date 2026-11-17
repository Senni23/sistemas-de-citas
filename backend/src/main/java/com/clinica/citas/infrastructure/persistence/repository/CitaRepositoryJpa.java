package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.infrastructure.persistence.mapper.CitaMapper;
import com.clinica.citas.infrastructure.persistence.springdata.CitaJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Implementacion del contrato CitaRepository del dominio usando Spring Data JPA y MySQL. */
@Repository
public class CitaRepositoryJpa implements CitaRepository {

    private final CitaJpaRepository jpa;
    private final CitaMapper mapper;

    public CitaRepositoryJpa(CitaJpaRepository jpa, CitaMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Cita guardar(Cita cita) {
        return mapper.toDomain(jpa.save(mapper.toEntity(cita)));
    }

    @Override
    public Optional<Cita> buscarPorId(CitaId id) {
        return jpa.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public List<Cita> listar() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cita> listarPorPaciente(PacienteId pacienteId) {
        return jpa.findByPacienteId(pacienteId.valor()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cita> listarPorMedico(MedicoId medicoId) {
        return jpa.findByMedicoId(medicoId.valor()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void eliminar(CitaId id) {
        jpa.deleteById(id.valor());
    }

    @Override
    public long contar() {
        return jpa.count();
    }

    @Override
    public long contarPorFecha(LocalDate fecha) {
        return jpa.countByFecha(fecha);
    }

    @Override
    public boolean existeCitaActiva(MedicoId medicoId, HorarioCita horario, CitaId excluir) {
        if (excluir == null) {
            return jpa.existsByMedicoIdAndFechaAndHoraAndEstadoNot(
                    medicoId.valor(), horario.fecha(), horario.hora(), EstadoCita.CANCELADA);
        }
        return jpa.existsByMedicoIdAndFechaAndHoraAndEstadoNotAndIdNot(
                medicoId.valor(), horario.fecha(), horario.hora(), EstadoCita.CANCELADA, excluir.valor());
    }
}
