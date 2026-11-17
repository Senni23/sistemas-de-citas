package com.clinica.citas.application.catalogo.view;

import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.catalogo.Servicio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Arma las vistas de servicios (con su especialidad) cargando todas las especialidades de una sola vez. */
@Component
public class ServicioViewFactory {

    private final EspecialidadRepository especialidades;

    public ServicioViewFactory(EspecialidadRepository especialidades) {
        this.especialidades = especialidades;
    }

    public ServicioView construir(Servicio servicio) {
        return construirLista(List.of(servicio)).get(0);
    }

    public List<ServicioView> construirLista(List<Servicio> servicios) {
        List<EspecialidadId> ids = servicios.stream().map(Servicio::especialidadId).distinct().toList();
        Map<EspecialidadId, Especialidad> porId = especialidades.listarPorIds(ids).stream()
                .collect(Collectors.toMap(Especialidad::id, Function.identity()));
        return servicios.stream().map(s -> new ServicioView(
                s.id().valor(), s.nombre(), s.precio().valor(), s.duracionEstimada(),
                porId.containsKey(s.especialidadId()) ? EspecialidadView.de(porId.get(s.especialidadId())) : null)).toList();
    }
}
