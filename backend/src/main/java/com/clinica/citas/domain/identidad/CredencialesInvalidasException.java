package com.clinica.citas.domain.identidad;

import com.clinica.citas.domain.shared.DomainException;

public class CredencialesInvalidasException extends DomainException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
