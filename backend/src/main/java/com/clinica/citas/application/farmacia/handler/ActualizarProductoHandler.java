package com.clinica.citas.application.farmacia.handler;

import com.clinica.citas.application.farmacia.command.FarmaciaCommands;
import com.clinica.citas.application.farmacia.view.ProductoView;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.farmacia.ProductoFarmacia;
import com.clinica.citas.domain.farmacia.ProductoFarmaciaRepository;
import com.clinica.citas.domain.farmacia.ProductoId;
import com.clinica.citas.domain.farmacia.Stock;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarProductoHandler implements CommandHandler<FarmaciaCommands.ActualizarProducto, ProductoView> {

    private final ProductoFarmaciaRepository productos;

    public ActualizarProductoHandler(ProductoFarmaciaRepository productos) {
        this.productos = productos;
    }

    @Override
    @Transactional
    public ProductoView handle(FarmaciaCommands.ActualizarProducto c) {
        ProductoFarmacia producto = RecursoNoEncontradoException.requerir(
                productos.buscarPorId(new ProductoId(c.id())), "Producto de farmacia no encontrado: " + c.id());
        producto.actualizar(c.nombre(), c.descripcion(), Stock.de(c.stock(), c.stockMinimo()), c.unidadMedida(),
                new Dinero(c.precioUnitario()), c.fechaVencimiento());
        return ProductoView.de(productos.guardar(producto));
    }
}
