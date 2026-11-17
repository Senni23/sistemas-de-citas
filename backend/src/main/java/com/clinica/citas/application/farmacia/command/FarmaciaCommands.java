package com.clinica.citas.application.farmacia.command;

import com.clinica.citas.application.farmacia.view.ProductoView;
import com.clinica.citas.application.shared.Command;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class FarmaciaCommands {

    private FarmaciaCommands() {
    }

    public record RegistrarProducto(String nombre, String descripcion, int stock, int stockMinimo, String unidadMedida,
                                    BigDecimal precioUnitario, LocalDate fechaVencimiento) implements Command<ProductoView> {
    }

    public record ActualizarProducto(Long id, String nombre, String descripcion, int stock, int stockMinimo,
                                     String unidadMedida, BigDecimal precioUnitario,
                                     LocalDate fechaVencimiento) implements Command<ProductoView> {
    }

    public record EliminarProducto(Long id) implements Command<Void> {
    }
}
