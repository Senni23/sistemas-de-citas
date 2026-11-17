package com.clinica.citas.infrastructure.persistence.springdata;

import com.clinica.citas.infrastructure.persistence.entity.ProductoFarmaciaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoFarmaciaJpaRepository extends JpaRepository<ProductoFarmaciaJpaEntity, Long> {

    Optional<ProductoFarmaciaJpaEntity> findByNombre(String nombre);
}
