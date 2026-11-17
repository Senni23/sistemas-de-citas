package com.clinica.citas.infrastructure.persistence.mapper;

import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.infrastructure.persistence.entity.ServicioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ServicioMapper {

    public Servicio toDomain(ServicioJpaEntity e) {
        return Servicio.reconstruir(new ServicioId(e.getId()), e.getNombre(), Dinero.reconstruir(e.getPrecio()),
                e.getDuracionEstimada(), new EspecialidadId(e.getEspecialidadId()));
    }

    public ServicioJpaEntity toEntity(Servicio s) {
        ServicioJpaEntity e = new ServicioJpaEntity();
        e.setId(s.id() != null ? s.id().valor() : null);
        e.setNombre(s.nombre());
        e.setPrecio(s.precio().valor());
        e.setDuracionEstimada(s.duracionEstimada());
        e.setEspecialidadId(s.especialidadId().valor());
        return e;
    }
}
