package com.clinica.citas.application.farmacia.handler;

import com.clinica.citas.application.farmacia.query.FarmaciaQueries;
import com.clinica.citas.application.farmacia.view.ProductoView;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.farmacia.ProductoFarmaciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarProductosHandler implements QueryHandler<FarmaciaQueries.ListarProductos, List<ProductoView>> {

    private final ProductoFarmaciaRepository productos;

    public ListarProductosHandler(ProductoFarmaciaRepository productos) {
        this.productos = productos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoView> handle(FarmaciaQueries.ListarProductos query) {
        return productos.listar().stream().map(ProductoView::de).toList();
    }
}
