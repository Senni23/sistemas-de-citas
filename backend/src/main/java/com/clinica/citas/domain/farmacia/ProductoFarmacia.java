package com.clinica.citas.domain.farmacia;

import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.DomainException;

import java.time.LocalDate;

/** Raiz de agregado: un producto del inventario de la farmacia. */
public final class ProductoFarmacia {

    private final ProductoId id;
    private String nombre;
    private String descripcion;
    private Stock stock;
    private String unidadMedida;
    private Dinero precioUnitario;
    private LocalDate fechaVencimiento;

    private ProductoFarmacia(ProductoId id, String nombre, String descripcion, Stock stock, String unidadMedida,
                             Dinero precioUnitario, LocalDate fechaVencimiento) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
        this.fechaVencimiento = fechaVencimiento;
    }

    public static ProductoFarmacia registrar(String nombre, String descripcion, Stock stock, String unidadMedida,
                                             Dinero precioUnitario, LocalDate fechaVencimiento) {
        return new ProductoFarmacia(null, validarNombre(nombre), validarDescripcion(descripcion), exigir(stock, "El stock es obligatorio"),
                validarUnidad(unidadMedida), exigir(precioUnitario, "El precio unitario es obligatorio"), fechaVencimiento);
    }

    public static ProductoFarmacia reconstruir(ProductoId id, String nombre, String descripcion, Stock stock,
                                               String unidadMedida, Dinero precioUnitario, LocalDate fechaVencimiento) {
        return new ProductoFarmacia(id, nombre, descripcion, stock, unidadMedida, precioUnitario, fechaVencimiento);
    }

    public void actualizar(String nombre, String descripcion, Stock stock, String unidadMedida, Dinero precioUnitario,
                           LocalDate fechaVencimiento) {
        this.nombre = validarNombre(nombre);
        this.descripcion = validarDescripcion(descripcion);
        this.stock = exigir(stock, "El stock es obligatorio");
        this.unidadMedida = validarUnidad(unidadMedida);
        this.precioUnitario = exigir(precioUnitario, "El precio unitario es obligatorio");
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean estaVencido(LocalDate hoy) {
        return fechaVencimiento != null && fechaVencimiento.isBefore(hoy);
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del producto es obligatorio");
        }
        if (nombre.trim().length() > 150) {
            throw new DomainException("El nombre del producto no puede pasar de 150 caracteres");
        }
        return nombre.trim();
    }

    private static String validarDescripcion(String descripcion) {
        if (descripcion != null && descripcion.length() > 255) {
            throw new DomainException("La descripcion no puede pasar de 255 caracteres");
        }
        return descripcion;
    }

    private static String validarUnidad(String unidad) {
        if (unidad != null && unidad.length() > 50) {
            throw new DomainException("La unidad de medida no puede pasar de 50 caracteres");
        }
        return unidad;
    }

    private static <T> T exigir(T valor, String mensaje) {
        if (valor == null) {
            throw new DomainException(mensaje);
        }
        return valor;
    }

    public ProductoId id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public String descripcion() {
        return descripcion;
    }

    public Stock stock() {
        return stock;
    }

    public String unidadMedida() {
        return unidadMedida;
    }

    public Dinero precioUnitario() {
        return precioUnitario;
    }

    public LocalDate fechaVencimiento() {
        return fechaVencimiento;
    }
}
