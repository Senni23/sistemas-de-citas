package com.clinica.citas.interfaces.rest.controller;

import com.clinica.citas.application.farmacia.command.FarmaciaCommands;
import com.clinica.citas.application.farmacia.handler.ActualizarProductoHandler;
import com.clinica.citas.application.farmacia.handler.EliminarProductoHandler;
import com.clinica.citas.application.farmacia.handler.ListarProductosHandler;
import com.clinica.citas.application.farmacia.handler.ObtenerProductoHandler;
import com.clinica.citas.application.farmacia.handler.RegistrarProductoHandler;
import com.clinica.citas.application.farmacia.query.FarmaciaQueries;
import com.clinica.citas.application.farmacia.view.ProductoView;
import com.clinica.citas.interfaces.rest.request.ProductoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmacia/productos")
public class ProductoFarmaciaController {

    private final ListarProductosHandler listar;
    private final ObtenerProductoHandler obtener;
    private final RegistrarProductoHandler registrar;
    private final ActualizarProductoHandler actualizar;
    private final EliminarProductoHandler eliminar;

    public ProductoFarmaciaController(ListarProductosHandler listar, ObtenerProductoHandler obtener,
                                      RegistrarProductoHandler registrar, ActualizarProductoHandler actualizar,
                                      EliminarProductoHandler eliminar) {
        this.listar = listar;
        this.obtener = obtener;
        this.registrar = registrar;
        this.actualizar = actualizar;
        this.eliminar = eliminar;
    }

    @GetMapping
    public List<ProductoView> listar() {
        return listar.handle(new FarmaciaQueries.ListarProductos());
    }

    @GetMapping("/{id}")
    public ProductoView obtener(@PathVariable Long id) {
        return obtener.handle(new FarmaciaQueries.ObtenerProducto(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoView crear(@Valid @RequestBody ProductoRequest r) {
        return registrar.handle(new FarmaciaCommands.RegistrarProducto(r.nombre(), r.descripcion(), r.stock(), r.stockMinimo(),
                r.unidadMedida(), r.precioUnitario(), r.fechaVencimiento()));
    }

    @PutMapping("/{id}")
    public ProductoView actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest r) {
        return actualizar.handle(new FarmaciaCommands.ActualizarProducto(id, r.nombre(), r.descripcion(), r.stock(),
                r.stockMinimo(), r.unidadMedida(), r.precioUnitario(), r.fechaVencimiento()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        eliminar.handle(new FarmaciaCommands.EliminarProducto(id));
    }
}
