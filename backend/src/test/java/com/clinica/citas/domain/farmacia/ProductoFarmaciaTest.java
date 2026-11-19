package com.clinica.citas.domain.farmacia;

import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductoFarmaciaTest {

    private ProductoFarmacia producto(LocalDate vence) {
        return ProductoFarmacia.registrar("Paracetamol 500mg", "Analgesico", Stock.de(40, 20), "caja", Dinero.de("8.5"), vence);
    }

    @Test
    @DisplayName("El stock no acepta cantidades negativas")
    void stock_noAceptaNegativos() {
        assertThatThrownBy(() -> Stock.de(-1, 5)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Stock.de(5, -1)).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Hay que reponer cuando el stock llega al minimo o baja de el")
    void stock_bajoElMinimo() {
        assertThat(Stock.de(21, 20).estaBajoElMinimo()).isFalse();
        assertThat(Stock.de(20, 20).estaBajoElMinimo()).isTrue();
        assertThat(Stock.de(8, 15).estaBajoElMinimo()).isTrue();
    }

    @Test
    @DisplayName("Un producto nuevo no tiene id; el nombre es obligatorio")
    void registrar_sinIdYNombreObligatorio() {
        assertThat(producto(null).id()).isNull();
        assertThatThrownBy(() -> ProductoFarmacia.registrar(" ", null, Stock.de(1, 1), null, Dinero.de("1"), null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Un producto esta vencido si su fecha de vencimiento ya paso")
    void estaVencido_segunLaFecha() {
        LocalDate hoy = LocalDate.of(2026, 10, 20);

        assertThat(producto(LocalDate.of(2026, 10, 19)).estaVencido(hoy)).isTrue();
        assertThat(producto(LocalDate.of(2026, 10, 20)).estaVencido(hoy)).isFalse();
        assertThat(producto(null).estaVencido(hoy)).isFalse();
    }

    @Test
    @DisplayName("Actualizar reemplaza todos los datos editables")
    void actualizar_reemplazaLosDatos() {
        ProductoFarmacia p = producto(null);

        p.actualizar("Ibuprofeno", "Antiinflamatorio", Stock.de(5, 2), "frasco", Dinero.de("12"), LocalDate.of(2027, 1, 1));

        assertThat(p.nombre()).isEqualTo("Ibuprofeno");
        assertThat(p.stock()).isEqualTo(Stock.de(5, 2));
        assertThat(p.precioUnitario().valor()).isEqualByComparingTo("12.00");
        assertThat(p.fechaVencimiento()).isEqualTo(LocalDate.of(2027, 1, 1));
    }
}
