package com.clinica.citas.domain.shared;

import java.util.List;

/**
 * Un trozo de un listado mas grande: los elementos de la pagina pedida y cuantos hay en total.
 * Es un tipo propio del dominio para que las capas de arriba no dependan de Spring Data.
 */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos) {

    public Pagina {
        contenido = List.copyOf(contenido);
    }

    public int totalPaginas() {
        return (int) Math.ceil((double) totalElementos / tamano);
    }

    /** Misma pagina (mismos numeros) pero con otro contenido, por ejemplo las vistas armadas a partir de los agregados. */
    public <R> Pagina<R> conContenido(List<R> nuevoContenido) {
        return new Pagina<>(nuevoContenido, pagina, tamano, totalElementos);
    }
}
