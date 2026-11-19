package com.clinica.citas.application.farmacia.handler;

import com.clinica.citas.application.farmacia.query.FarmaciaQueries;
import com.clinica.citas.application.farmacia.view.ProductoView;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.farmacia.ProductoFarmaciaRepository;
import com.clinica.citas.domain.farmacia.ProductoId;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerProductoHandler implements QueryHandler<FarmaciaQueries.ObtenerProducto, ProductoView> {

    private final ProductoFarmaciaRepository productos;

    public ObtenerProductoHandler(ProductoFarmaciaRepository productos) {
        this.productos = productos;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoView handle(FarmaciaQueries.ObtenerProducto query) {
        return ProductoView.de(RecursoNoEncontradoException.requerir(
                productos.buscarPorId(new ProductoId(query.id())), "Producto de farmacia no encontrado: " + query.id()));
    }
}
