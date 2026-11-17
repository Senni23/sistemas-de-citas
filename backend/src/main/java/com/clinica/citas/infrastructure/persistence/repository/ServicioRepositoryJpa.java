package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.catalogo.ServicioRepository;
import com.clinica.citas.infrastructure.persistence.mapper.ServicioMapper;
import com.clinica.citas.infrastructure.persistence.springdata.ServicioJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class ServicioRepositoryJpa implements ServicioRepository {

    private final ServicioJpaRepository jpa;
    private final ServicioMapper mapper;

    public ServicioRepositoryJpa(ServicioJpaRepository jpa, ServicioMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        return mapper.toDomain(jpa.save(mapper.toEntity(servicio)));
    }

    @Override
    public Optional<Servicio> buscarPorId(ServicioId id) {
        return jpa.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public Optional<Servicio> buscarPorNombreYEspecialidad(String nombre, EspecialidadId especialidadId) {
        return jpa.findByNombreAndEspecialidadId(nombre, especialidadId.valor()).map(mapper::toDomain);
    }

    @Override
    public List<Servicio> listar() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Servicio> listarPorEspecialidad(EspecialidadId especialidadId) {
        return jpa.findByEspecialidadId(especialidadId.valor()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Servicio> listarPorIds(Collection<ServicioId> ids) {
        return jpa.findAllById(ids.stream().map(ServicioId::valor).toList()).stream().map(mapper::toDomain).toList();
    }
}
