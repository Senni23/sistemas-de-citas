package com.clinica.citas.infrastructure.persistence.mapper;

import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.infrastructure.persistence.entity.PacienteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper {

    public Paciente toDomain(PacienteJpaEntity e) {
        return Paciente.reconstruir(new PacienteId(e.getId()), new NombrePersona(e.getNombres(), e.getApellidos()),
                Dni.reconstruir(e.getDni()), e.getFechaNacimiento(), e.getTelefono(),
                CorreoElectronico.reconstruir(e.getEmail()), e.getDireccion());
    }

    public PacienteJpaEntity toEntity(Paciente p) {
        PacienteJpaEntity e = new PacienteJpaEntity();
        e.setId(p.id() != null ? p.id().valor() : null);
        e.setNombres(p.nombre().nombres());
        e.setApellidos(p.nombre().apellidos());
        e.setDni(p.dni().valor());
        e.setFechaNacimiento(p.fechaNacimiento());
        e.setTelefono(p.telefono());
        e.setEmail(p.correo() != null ? p.correo().valor() : null);
        e.setDireccion(p.direccion());
        return e;
    }
}
