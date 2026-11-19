package com.clinica.citas.infrastructure.persistence.mapper;

import com.clinica.citas.domain.farmacia.ProductoFarmacia;
import com.clinica.citas.domain.farmacia.ProductoId;
import com.clinica.citas.domain.farmacia.Stock;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.infrastructure.persistence.entity.ProductoFarmaciaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductoFarmaciaMapper {

    public ProductoFarmacia toDomain(ProductoFarmaciaJpaEntity e) {
        return ProductoFarmacia.reconstruir(new ProductoId(e.getId()), e.getNombre(), e.getDescripcion(),
                Stock.reconstruir(e.getStock(), e.getStockMinimo()), e.getUnidadMedida(),
                Dinero.reconstruir(e.getPrecioUnitario()), e.getFechaVencimiento());
    }

    public ProductoFarmaciaJpaEntity toEntity(ProductoFarmacia p) {
        ProductoFarmaciaJpaEntity e = new ProductoFarmaciaJpaEntity();
        e.setId(p.id() != null ? p.id().valor() : null);
        e.setNombre(p.nombre());
        e.setDescripcion(p.descripcion());
        e.setStock(p.stock().actual());
        e.setStockMinimo(p.stock().minimo());
        e.setUnidadMedida(p.unidadMedida());
        e.setPrecioUnitario(p.precioUnitario().valor());
        e.setFechaVencimiento(p.fechaVencimiento());
        return e;
    }
}
