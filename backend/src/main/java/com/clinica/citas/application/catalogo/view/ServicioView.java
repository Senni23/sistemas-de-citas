package com.clinica.citas.application.catalogo.view;

import java.math.BigDecimal;

public record ServicioView(Long id, String nombre, BigDecimal precio, String duracionEstimada,
                           EspecialidadView especialidad) {
}
