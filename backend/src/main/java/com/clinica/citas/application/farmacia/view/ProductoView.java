package com.clinica.citas.application.farmacia.view;

import com.clinica.citas.domain.farmacia.ProductoFarmacia;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductoView(Long id, String nombre, String descripcion, Integer stock, Integer stockMinimo,
                           String unidadMedida, BigDecimal precioUnitario, LocalDate fechaVencimiento) {

    public static ProductoView de(ProductoFarmacia p) {
        return new ProductoView(p.id().valor(), p.nombre(), p.descripcion(), p.stock().actual(), p.stock().minimo(),
                p.unidadMedida(), p.precioUnitario().valor(), p.fechaVencimiento());
    }
}
