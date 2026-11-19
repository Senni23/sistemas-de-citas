package com.clinica.citas.domain.shared;

import java.util.Optional;

public class RecursoNoEncontradoException extends DomainException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    /** Devuelve el valor si existe; si no, falla con el mensaje indicado (por ejemplo "Cita no encontrada: 5"). */
    public static <T> T requerir(Optional<T> valor, String mensaje) {
        return valor.orElseThrow(() -> new RecursoNoEncontradoException(mensaje));
    }
}
