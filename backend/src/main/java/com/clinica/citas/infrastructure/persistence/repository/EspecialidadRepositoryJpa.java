package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.infrastructure.persistence.mapper.EspecialidadMapper;
import com.clinica.citas.infrastructure.persistence.springdata.EspecialidadJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class EspecialidadRepositoryJpa implements EspecialidadRepository {

    private final EspecialidadJpaRepository jpa;
    private final EspecialidadMapper mapper;

    public EspecialidadRepositoryJpa(EspecialidadJpaRepository jpa, EspecialidadMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Especialidad guardar(Especialidad especialidad) {
        return mapper.toDomain(jpa.save(mapper.toEntity(especialidad)));
    }

    @Override
    public Optional<Especialidad> buscarPorId(EspecialidadId id) {
        return jpa.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public Optional<Especialidad> buscarPorNombre(String nombre) {
        return jpa.findByNombre(nombre).map(mapper::toDomain);
    }

    @Override
    public List<Especialidad> listar() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Especialidad> listarPorIds(Collection<EspecialidadId> ids) {
        return jpa.findAllById(ids.stream().map(EspecialidadId::valor).toList()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void eliminar(EspecialidadId id) {
        jpa.deleteById(id.valor());
    }
}
