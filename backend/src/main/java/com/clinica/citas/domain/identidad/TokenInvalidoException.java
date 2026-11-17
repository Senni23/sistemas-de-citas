package com.clinica.citas.domain.identidad;

import com.clinica.citas.domain.shared.DomainException;

public class TokenInvalidoException extends DomainException {

    public TokenInvalidoException(String mensaje) {
        super(mensaje);
    }
}
