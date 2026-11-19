package com.clinica.citas.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaginacionTest {

    @Test
    @DisplayName("Una solicitud de pagina valida se crea con la pagina y el tamano pedidos")
    void solicitudValida() {
        SolicitudDePagina solicitud = new SolicitudDePagina(2, 25);

        assertThat(solicitud.pagina()).isEqualTo(2);
        assertThat(solicitud.tamano()).isEqualTo(25);
    }

    @Test
    @DisplayName("El tamano maximo permitido es 100")
    void tamanoMaximo() {
        assertThat(new SolicitudDePagina(0, 100).tamano()).isEqualTo(100);
        assertThatThrownBy(() -> new SolicitudDePagina(0, 101)).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("No se aceptan paginas negativas ni tamanos menores a 1")
    void limitesInvalidos() {
        assertThatThrownBy(() -> new SolicitudDePagina(-1, 10))
                .isInstanceOf(DomainException.class).hasMessageContaining("pagina");
        assertThatThrownBy(() -> new SolicitudDePagina(0, 0))
                .isInstanceOf(DomainException.class).hasMessageContaining("tamano");
    }

    @Test
    @DisplayName("El total de paginas se redondea hacia arriba")
    void totalDePaginas() {
        assertThat(new Pagina<>(List.of(1, 2), 0, 2, 5).totalPaginas()).isEqualTo(3);
        assertThat(new Pagina<>(List.of(1, 2), 0, 2, 4).totalPaginas()).isEqualTo(2);
        assertThat(new Pagina<Integer>(List.of(), 0, 10, 0).totalPaginas()).isZero();
    }

    @Test
    @DisplayName("Cambiar el contenido de una pagina conserva los numeros de pagina, tamano y total")
    void conContenido() {
        Pagina<Integer> numeros = new Pagina<>(List.of(1, 2), 1, 2, 7);

        Pagina<String> textos = numeros.conContenido(List.of("uno", "dos"));

        assertThat(textos.contenido()).containsExactly("uno", "dos");
        assertThat(textos.pagina()).isEqualTo(1);
        assertThat(textos.tamano()).isEqualTo(2);
        assertThat(textos.totalElementos()).isEqualTo(7);
    }

    @Test
    @DisplayName("La pagina guarda una copia: cambiar la lista original no la altera")
    void esInmutable() {
        List<Integer> original = new ArrayList<>(List.of(1, 2));
        Pagina<Integer> pagina = new Pagina<>(original, 0, 2, 2);

        original.add(3);

        assertThat(pagina.contenido()).containsExactly(1, 2);
        assertThatThrownBy(() -> pagina.contenido().add(9)).isInstanceOf(UnsupportedOperationException.class);
    }
}
