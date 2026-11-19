package com.clinica.citas.domain.identidad;

import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;

import java.util.Optional;

/** Contrato de persistencia de cuentas de usuario. */
public interface UsuarioRepository {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorTokenRecuperacion(String token);

    void eliminarPorMedico(MedicoId medicoId);

    void eliminarPorPaciente(PacienteId pacienteId);
}
