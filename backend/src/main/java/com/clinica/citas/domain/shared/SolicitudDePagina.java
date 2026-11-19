package com.clinica.citas.domain.shared;

/**
 * Objeto de valor: que pagina de un listado se pide y de que tamano. Valida los limites para que nadie
 * pueda pedir una pagina negativa ni traer miles de registros de una sola vez.
 */
public record SolicitudDePagina(int pagina, int tamano) {

    public static final int TAMANO_MAXIMO = 100;

    public SolicitudDePagina {
        if (pagina < 0) {
            throw new DomainException("El numero de pagina no puede ser negativo");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new DomainException("El tamano de pagina debe estar entre 1 y " + TAMANO_MAXIMO);
        }
    }
}
