package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.domain.medicos.Medico;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.infrastructure.persistence.mapper.MedicoMapper;
import com.clinica.citas.infrastructure.persistence.springdata.MedicoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicoRepositoryJpa implements MedicoRepository {

    private final MedicoJpaRepository jpa;
    private final MedicoMapper mapper;

    public MedicoRepositoryJpa(MedicoJpaRepository jpa, MedicoMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Medico guardar(Medico medico) {
        return mapper.toDomain(jpa.save(mapper.toEntity(medico)));
    }

    @Override
    public Optional<Medico> buscarPorId(MedicoId id) {
        return jpa.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public Optional<Medico> buscarPorCorreo(String correo) {
        return jpa.findByEmail(correo).map(mapper::toDomain);
    }

    @Override
    public List<Medico> listar() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Medico> listarPorIds(Collection<MedicoId> ids) {
        return jpa.findAllById(ids.stream().map(MedicoId::valor).toList()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void eliminar(MedicoId id) {
        jpa.deleteById(id.valor());
    }

    @Override
    public long contar() {
        return jpa.count();
    }
}
