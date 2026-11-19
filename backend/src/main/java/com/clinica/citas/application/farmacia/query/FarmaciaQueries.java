package com.clinica.citas.application.farmacia.query;

import com.clinica.citas.application.farmacia.view.ProductoView;
import com.clinica.citas.application.shared.Query;

import java.util.List;

public final class FarmaciaQueries {

    private FarmaciaQueries() {
    }

    public record ListarProductos() implements Query<List<ProductoView>> {
    }

    public record ObtenerProducto(Long id) implements Query<ProductoView> {
    }
}
