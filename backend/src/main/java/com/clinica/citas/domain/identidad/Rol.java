package com.clinica.citas.domain.identidad;

/** Los cuatro tipos de usuario del sistema. */
public enum Rol {
    ADMIN,
    RECEPCIONISTA,
    MEDICO,
    PACIENTE;

    /** El personal de la clinica (administrador y recepcionista) puede gestionar todo el sistema. */
    public boolean esPersonalClinica() {
        return this == ADMIN || this == RECEPCIONISTA;
    }
}
