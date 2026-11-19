package com.clinica.citas.infrastructure.persistence.mapper;

import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.medicos.Colegiatura;
import com.clinica.citas.domain.medicos.Medico;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.infrastructure.persistence.entity.MedicoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper {

    public Medico toDomain(MedicoJpaEntity e) {
        return Medico.reconstruir(new MedicoId(e.getId()), new NombrePersona(e.getNombres(), e.getApellidos()),
                Colegiatura.reconstruir(e.getColegiatura()), e.getTelefono(), CorreoElectronico.reconstruir(e.getEmail()),
                new EspecialidadId(e.getEspecialidadId()));
    }

    public MedicoJpaEntity toEntity(Medico m) {
        MedicoJpaEntity e = new MedicoJpaEntity();
        e.setId(m.id() != null ? m.id().valor() : null);
        e.setNombres(m.nombre().nombres());
        e.setApellidos(m.nombre().apellidos());
        e.setColegiatura(m.colegiatura().valor());
        e.setTelefono(m.telefono());
        e.setEmail(m.correo() != null ? m.correo().valor() : null);
        e.setEspecialidadId(m.especialidadId().valor());
        return e;
    }
}
