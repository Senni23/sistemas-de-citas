package com.clinica.citas.infrastructure.persistence.mapper;

import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.infrastructure.persistence.entity.EspecialidadJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EspecialidadMapper {

    public Especialidad toDomain(EspecialidadJpaEntity e) {
        return Especialidad.reconstruir(new EspecialidadId(e.getId()), e.getNombre(), e.getDescripcion());
    }

    public EspecialidadJpaEntity toEntity(Especialidad s) {
        EspecialidadJpaEntity e = new EspecialidadJpaEntity();
        e.setId(s.id() != null ? s.id().valor() : null);
        e.setNombre(s.nombre());
        e.setDescripcion(s.descripcion());
        return e;
    }
}
