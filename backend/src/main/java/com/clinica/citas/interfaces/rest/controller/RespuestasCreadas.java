package com.clinica.citas.interfaces.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Arma la respuesta "201 Created" de los POST: devuelve el recurso creado y, en la cabecera Location, la
 * direccion donde se puede consultar (por ejemplo /api/citas/15), como pide el estandar REST.
 */
final class RespuestasCreadas {

    private RespuestasCreadas() {
    }

    static <T> ResponseEntity<T> creado(T recurso, Long id) {
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(ubicacion).body(recurso);
    }
}
