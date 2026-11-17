package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.infrastructure.persistence.mapper.PacienteMapper;
import com.clinica.citas.infrastructure.persistence.springdata.PacienteJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class PacienteRepositoryJpa implements PacienteRepository {

    private final PacienteJpaRepository jpa;
    private final PacienteMapper mapper;

    public PacienteRepositoryJpa(PacienteJpaRepository jpa, PacienteMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Paciente guardar(Paciente paciente) {
        return mapper.toDomain(jpa.save(mapper.toEntity(paciente)));
    }

    @Override
    public Optional<Paciente> buscarPorId(PacienteId id) {
        return jpa.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public Optional<Paciente> buscarPorDni(Dni dni) {
        return jpa.findByDni(dni.valor()).map(mapper::toDomain);
    }

    @Override
    public List<Paciente> listar() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Paciente> listarPorIds(Collection<PacienteId> ids) {
        return jpa.findAllById(ids.stream().map(PacienteId::valor).toList()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void eliminar(PacienteId id) {
        jpa.deleteById(id.valor());
    }

    @Override
    public long contar() {
        return jpa.count();
    }
}
