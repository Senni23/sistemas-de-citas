package com.clinica.citas.application.shared;

import com.clinica.citas.domain.shared.Pagina;

import java.util.List;

/** Respuesta de un listado paginado: los elementos de la pagina y los datos para armar los botones de navegacion. */
public record PaginaView<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {

    public static <T> PaginaView<T> de(Pagina<T> pagina) {
        return new PaginaView<>(pagina.contenido(), pagina.pagina(), pagina.tamano(), pagina.totalElementos(),
                pagina.totalPaginas());
    }
}
