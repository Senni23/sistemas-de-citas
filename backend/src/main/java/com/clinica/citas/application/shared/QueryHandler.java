package com.clinica.citas.application.shared;

/** Responde una consulta sin modificar nada. */
public interface QueryHandler<Q extends Query<R>, R> {

    R handle(Q query);
}
