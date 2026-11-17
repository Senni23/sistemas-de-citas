package com.clinica.citas.domain.farmacia;

import java.util.List;
import java.util.Optional;

/** Contrato de persistencia del inventario de farmacia. */
public interface ProductoFarmaciaRepository {

    ProductoFarmacia guardar(ProductoFarmacia producto);

    Optional<ProductoFarmacia> buscarPorId(ProductoId id);

    Optional<ProductoFarmacia> buscarPorNombre(String nombre);

    List<ProductoFarmacia> listar();

    void eliminar(ProductoId id);
}
