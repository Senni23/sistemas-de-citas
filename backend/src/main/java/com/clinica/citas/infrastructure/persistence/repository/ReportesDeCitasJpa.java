package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.application.dashboard.port.ReportesDeCitas;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.infrastructure.persistence.springdata.CitaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementacion del puerto de reportes con consultas directas a la base: una consulta nativa (SQL) para los
 * conteos por estado y una consulta JPQL con JOIN para los conteos por especialidad.
 */
@Repository
public class ReportesDeCitasJpa implements ReportesDeCitas {

    private final CitaJpaRepository jpa;

    public ReportesDeCitasJpa(CitaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<ConteoPorEstado> citasPorEstado() {
        // Consulta nativa: cada fila llega como {estado (texto), total (numero)}.
        return jpa.contarPorEstado().stream()
                .map(fila -> new ConteoPorEstado(EstadoCita.valueOf((String) fila[0]), ((Number) fila[1]).longValue()))
                .toList();
    }

    @Override
    public List<ConteoPorEspecialidad> citasActivasPorEspecialidad() {
        return jpa.contarPorEspecialidad(EstadoCita.CANCELADA).stream()
                .map(fila -> new ConteoPorEspecialidad(fila.getEspecialidad(), fila.getTotal()))
                .toList();
    }
}
