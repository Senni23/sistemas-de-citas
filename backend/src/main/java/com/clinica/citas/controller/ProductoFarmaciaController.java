package com.clinica.citas.controller;

import com.clinica.citas.model.ProductoFarmacia;
import com.clinica.citas.service.ProductoFarmaciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmacia/productos")
public class ProductoFarmaciaController {

    private final ProductoFarmaciaService productoFarmaciaService;

    public ProductoFarmaciaController(ProductoFarmaciaService productoFarmaciaService) {
        this.productoFarmaciaService = productoFarmaciaService;
    }

    @GetMapping
    public List<ProductoFarmacia> listar() {
        return productoFarmaciaService.listar();
    }

    @GetMapping("/{id}")
    public ProductoFarmacia obtener(@PathVariable Long id) {
        return productoFarmaciaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoFarmacia crear(@Valid @RequestBody ProductoFarmacia producto) {
        return productoFarmaciaService.crear(producto);
    }

    @PutMapping("/{id}")
    public ProductoFarmacia actualizar(@PathVariable Long id, @Valid @RequestBody ProductoFarmacia producto) {
        return productoFarmaciaService.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoFarmaciaService.eliminar(id);
    }
}
