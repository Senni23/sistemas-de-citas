package com.clinica.citas.service;

import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.ProductoFarmacia;
import com.clinica.citas.repository.ProductoFarmaciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoFarmaciaService {

    private final ProductoFarmaciaRepository productoFarmaciaRepository;

    public ProductoFarmaciaService(ProductoFarmaciaRepository productoFarmaciaRepository) {
        this.productoFarmaciaRepository = productoFarmaciaRepository;
    }

    public List<ProductoFarmacia> listar() {
        return productoFarmaciaRepository.findAll();
    }

    public ProductoFarmacia obtener(Long id) {
        return productoFarmaciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto de farmacia no encontrado: " + id));
    }

    public ProductoFarmacia crear(ProductoFarmacia producto) {
        producto.setId(null);
        return productoFarmaciaRepository.save(producto);
    }

    public ProductoFarmacia actualizar(Long id, ProductoFarmacia datos) {
        ProductoFarmacia producto = obtener(id);
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setStock(datos.getStock());
        producto.setStockMinimo(datos.getStockMinimo());
        producto.setUnidadMedida(datos.getUnidadMedida());
        producto.setPrecioUnitario(datos.getPrecioUnitario());
        producto.setFechaVencimiento(datos.getFechaVencimiento());
        return productoFarmaciaRepository.save(producto);
    }

    public void eliminar(Long id) {
        ProductoFarmacia producto = obtener(id);
        productoFarmaciaRepository.delete(producto);
    }
}
