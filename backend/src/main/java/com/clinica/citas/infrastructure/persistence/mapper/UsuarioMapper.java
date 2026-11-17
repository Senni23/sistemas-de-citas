package com.clinica.citas.infrastructure.persistence.mapper;

import com.clinica.citas.domain.identidad.PasswordHash;
import com.clinica.citas.domain.identidad.TokenRecuperacion;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioId;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioJpaEntity e) {
        TokenRecuperacion token = e.getResetToken() != null && !e.getResetToken().isBlank()
                ? new TokenRecuperacion(e.getResetToken(), e.getResetTokenExpira())
                : null;
        return Usuario.reconstruir(new UsuarioId(e.getId()), e.getUsername(), new PasswordHash(e.getPassword()), e.getRol(),
                e.getMedicoId() != null ? new MedicoId(e.getMedicoId()) : null,
                e.getPacienteId() != null ? new PacienteId(e.getPacienteId()) : null,
                token);
    }

    public UsuarioJpaEntity toEntity(Usuario u) {
        UsuarioJpaEntity e = new UsuarioJpaEntity();
        e.setId(u.id() != null ? u.id().valor() : null);
        e.setUsername(u.username());
        e.setPassword(u.passwordHash().valor());
        e.setRol(u.rol());
        e.setMedicoId(u.medicoId() != null ? u.medicoId().valor() : null);
        e.setPacienteId(u.pacienteId() != null ? u.pacienteId().valor() : null);
        e.setResetToken(u.tokenRecuperacion() != null ? u.tokenRecuperacion().valor() : null);
        e.setResetTokenExpira(u.tokenRecuperacion() != null ? u.tokenRecuperacion().expira() : null);
        return e;
    }
}
