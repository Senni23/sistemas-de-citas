package com.clinica.citas.application.dashboard.view;

import java.util.List;

/**
 * Totales del panel de administracion. Los cuatro primeros campos ya existian; los dos reportes se agregaron
 * al incorporar las consultas JPQL y nativa (el frontend los puede ignorar sin problema).
 */
public record ResumenDashboardView(long totalPacientes, long totalMedicos, long totalCitas, long citasHoy,
                                   List<CitasPorEstado> citasPorEstado, List<CitasPorEspecialidad> citasPorEspecialidad) {

    public record CitasPorEstado(String estado, long total) {
    }

    public record CitasPorEspecialidad(String especialidad, long total) {
    }
}
