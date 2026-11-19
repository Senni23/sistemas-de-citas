package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.infrastructure.persistence.mapper.UsuarioMapper;
import com.clinica.citas.infrastructure.persistence.springdata.UsuarioJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositoryJpa implements UsuarioRepository {

    private final UsuarioJpaRepository jpa;
    private final UsuarioMapper mapper;

    public UsuarioRepositoryJpa(UsuarioJpaRepository jpa, UsuarioMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return mapper.toDomain(jpa.save(mapper.toEntity(usuario)));
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return jpa.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorTokenRecuperacion(String token) {
        return jpa.findByResetToken(token).map(mapper::toDomain);
    }

    @Override
    public void eliminarPorMedico(MedicoId medicoId) {
        jpa.deleteByMedicoId(medicoId.valor());
    }

    @Override
    public void eliminarPorPaciente(PacienteId pacienteId) {
        jpa.deleteByPacienteId(pacienteId.valor());
    }
}
