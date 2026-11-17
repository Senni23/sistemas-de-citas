package com.clinica.citas.application.farmacia;

import com.clinica.citas.application.farmacia.command.FarmaciaCommands;
import com.clinica.citas.application.farmacia.handler.ActualizarProductoHandler;
import com.clinica.citas.application.farmacia.handler.EliminarProductoHandler;
import com.clinica.citas.application.farmacia.handler.ObtenerProductoHandler;
import com.clinica.citas.application.farmacia.handler.RegistrarProductoHandler;
import com.clinica.citas.application.farmacia.query.FarmaciaQueries;
import com.clinica.citas.application.farmacia.view.ProductoView;
import com.clinica.citas.domain.farmacia.ProductoFarmacia;
import com.clinica.citas.domain.farmacia.ProductoFarmaciaRepository;
import com.clinica.citas.domain.farmacia.ProductoId;
import com.clinica.citas.domain.farmacia.Stock;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.DomainException;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FarmaciaHandlersTest {

    @Mock
    private ProductoFarmaciaRepository productos;

    private ProductoFarmacia existente() {
        return ProductoFarmacia.reconstruir(new ProductoId(7L), "Paracetamol", "Analgesico", Stock.reconstruir(40, 20),
                "caja", Dinero.de("8.5"), null);
    }

    @Test
    @DisplayName("Al registrar un producto se crea uno nuevo (sin id) y la base le asigna el id")
    void registrar_creaProductoNuevoSinId() {
        when(productos.guardar(any())).thenAnswer(invocacion -> {
            ProductoFarmacia p = invocacion.getArgument(0);
            return ProductoFarmacia.reconstruir(new ProductoId(99L), p.nombre(), p.descripcion(), p.stock(),
                    p.unidadMedida(), p.precioUnitario(), p.fechaVencimiento());
        });

        ProductoView vista = new RegistrarProductoHandler(productos).handle(new FarmaciaCommands.RegistrarProducto(
                "Gasas", "Esteriles", 8, 15, "paquete", new BigDecimal("12.5"), null));

        ArgumentCaptor<ProductoFarmacia> captor = ArgumentCaptor.forClass(ProductoFarmacia.class);
        verify(productos).guardar(captor.capture());
        assertThat(captor.getValue().id()).isNull();
        assertThat(vista.id()).isEqualTo(99L);
        assertThat(vista.precioUnitario()).isEqualByComparingTo("12.50");
    }

    @Test
    @DisplayName("No se registra un producto con stock negativo")
    void registrar_stockNegativo_lanzaError() {
        assertThatThrownBy(() -> new RegistrarProductoHandler(productos).handle(new FarmaciaCommands.RegistrarProducto(
                "Gasas", null, -1, 5, null, BigDecimal.ONE, null))).isInstanceOf(DomainException.class);
        verify(productos, never()).guardar(any());
    }

    @Test
    @DisplayName("Actualizar copia todos los campos editables sobre el producto existente")
    void actualizar_copiaLosCampos() {
        when(productos.buscarPorId(new ProductoId(7L))).thenReturn(Optional.of(existente()));
        when(productos.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        new ActualizarProductoHandler(productos).handle(new FarmaciaCommands.ActualizarProducto(
                7L, "Ibuprofeno", "Antiinflamatorio", 10, 5, "frasco", new BigDecimal("9.8"), LocalDate.of(2027, 3, 15)));

        ArgumentCaptor<ProductoFarmacia> captor = ArgumentCaptor.forClass(ProductoFarmacia.class);
        verify(productos).guardar(captor.capture());
        ProductoFarmacia guardado = captor.getValue();
        assertThat(guardado.id()).isEqualTo(new ProductoId(7L));
        assertThat(guardado.nombre()).isEqualTo("Ibuprofeno");
        assertThat(guardado.stock()).isEqualTo(Stock.de(10, 5));
        assertThat(guardado.precioUnitario().valor()).isEqualByComparingTo("9.80");
        assertThat(guardado.fechaVencimiento()).isEqualTo(LocalDate.of(2027, 3, 15));
    }

    @Test
    @DisplayName("Buscar o eliminar un producto inexistente lanza RecursoNoEncontradoException")
    void productoInexistente_lanzaNoEncontrado() {
        when(productos.buscarPorId(new ProductoId(404L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new ObtenerProductoHandler(productos).handle(new FarmaciaQueries.ObtenerProducto(404L)))
                .isInstanceOf(RecursoNoEncontradoException.class).hasMessage("Producto de farmacia no encontrado: 404");
        assertThatThrownBy(() -> new EliminarProductoHandler(productos).handle(new FarmaciaCommands.EliminarProducto(404L)))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(productos, never()).eliminar(any());
    }

    @Test
    @DisplayName("Eliminar borra el producto existente")
    void eliminar_borraElProducto() {
        when(productos.buscarPorId(new ProductoId(7L))).thenReturn(Optional.of(existente()));

        new EliminarProductoHandler(productos).handle(new FarmaciaCommands.EliminarProducto(7L));

        verify(productos).eliminar(new ProductoId(7L));
    }
}
