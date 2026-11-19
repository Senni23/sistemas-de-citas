package com.clinica.citas.application.dashboard.port;

import com.clinica.citas.domain.citas.EstadoCita;

import java.util.List;

/**
 * Puerto de lectura para los reportes del tablero. Son conteos que cruzan varios agregados (citas, medicos
 * y especialidades), asi que no pasan por los agregados: la infraestructura los resuelve con consultas
 * directas a la base y la aplicacion solo recibe los resultados.
 */
public interface ReportesDeCitas {

    /** Cuantas citas hay en cada estado. */
    List<ConteoPorEstado> citasPorEstado();

    /** Cuantas citas activas (no canceladas) tiene cada especialidad, de mayor a menor. */
    List<ConteoPorEspecialidad> citasActivasPorEspecialidad();

    record ConteoPorEstado(EstadoCita estado, long total) {
    }

    record ConteoPorEspecialidad(String especialidad, long total) {
    }
}
