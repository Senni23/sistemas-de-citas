package com.clinica.citas.application.farmacia.handler;

import com.clinica.citas.application.farmacia.command.FarmaciaCommands;
import com.clinica.citas.application.farmacia.view.ProductoView;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.farmacia.ProductoFarmacia;
import com.clinica.citas.domain.farmacia.ProductoFarmaciaRepository;
import com.clinica.citas.domain.farmacia.Stock;
import com.clinica.citas.domain.shared.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: agregar un producto al inventario (el id siempre lo asigna la base de datos). */
@Service
public class RegistrarProductoHandler implements CommandHandler<FarmaciaCommands.RegistrarProducto, ProductoView> {

    private final ProductoFarmaciaRepository productos;

    public RegistrarProductoHandler(ProductoFarmaciaRepository productos) {
        this.productos = productos;
    }

    @Override
    @Transactional
    public ProductoView handle(FarmaciaCommands.RegistrarProducto c) {
        ProductoFarmacia producto = ProductoFarmacia.registrar(c.nombre(), c.descripcion(),
                Stock.de(c.stock(), c.stockMinimo()), c.unidadMedida(), new Dinero(c.precioUnitario()), c.fechaVencimiento());
        return ProductoView.de(productos.guardar(producto));
    }
}
