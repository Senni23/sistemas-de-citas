package com.clinica.citas.application.medicos.view;

import com.clinica.citas.application.catalogo.view.EspecialidadView;

public record MedicoView(Long id, String nombres, String apellidos, String colegiatura, String telefono,
                         String email, EspecialidadView especialidad) {
}
