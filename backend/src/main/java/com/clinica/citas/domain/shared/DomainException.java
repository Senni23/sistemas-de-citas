package com.clinica.citas.domain.shared;

/** Base de todas las reglas de negocio que se rompen en el dominio. */
public class DomainException extends RuntimeException {

    public DomainException(String mensaje) {
        super(mensaje);
    }
}
