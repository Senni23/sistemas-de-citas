package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.shared.DomainException;

public class HorarioOcupadoException extends DomainException {

    public HorarioOcupadoException(String mensaje) {
        super(mensaje);
    }
}
