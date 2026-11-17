package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.domain.farmacia.ProductoFarmacia;
import com.clinica.citas.domain.farmacia.ProductoFarmaciaRepository;
import com.clinica.citas.domain.farmacia.ProductoId;
import com.clinica.citas.infrastructure.persistence.mapper.ProductoFarmaciaMapper;
import com.clinica.citas.infrastructure.persistence.springdata.ProductoFarmaciaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductoFarmaciaRepositoryJpa implements ProductoFarmaciaRepository {

    private final ProductoFarmaciaJpaRepository jpa;
    private final ProductoFarmaciaMapper mapper;

    public ProductoFarmaciaRepositoryJpa(ProductoFarmaciaJpaRepository jpa, ProductoFarmaciaMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public ProductoFarmacia guardar(ProductoFarmacia producto) {
        return mapper.toDomain(jpa.save(mapper.toEntity(producto)));
    }

    @Override
    public Optional<ProductoFarmacia> buscarPorId(ProductoId id) {
        return jpa.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public Optional<ProductoFarmacia> buscarPorNombre(String nombre) {
        return jpa.findByNombre(nombre).map(mapper::toDomain);
    }

    @Override
    public List<ProductoFarmacia> listar() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void eliminar(ProductoId id) {
        jpa.deleteById(id.valor());
    }
}
