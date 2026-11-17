package com.clinica.citas.application.farmacia.handler;

import com.clinica.citas.application.farmacia.command.FarmaciaCommands;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.farmacia.ProductoFarmaciaRepository;
import com.clinica.citas.domain.farmacia.ProductoId;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarProductoHandler implements CommandHandler<FarmaciaCommands.EliminarProducto, Void> {

    private final ProductoFarmaciaRepository productos;

    public EliminarProductoHandler(ProductoFarmaciaRepository productos) {
        this.productos = productos;
    }

    @Override
    @Transactional
    public Void handle(FarmaciaCommands.EliminarProducto c) {
        ProductoId id = new ProductoId(c.id());
        RecursoNoEncontradoException.requerir(productos.buscarPorId(id), "Producto de farmacia no encontrado: " + c.id());
        productos.eliminar(id);
        return null;
    }
}
