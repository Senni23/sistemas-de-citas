package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.shared.DomainException;

public class HorarioInvalidoException extends DomainException {

    public HorarioInvalidoException(String mensaje) {
        super(mensaje);
    }
}
