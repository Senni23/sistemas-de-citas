package com.clinica.citas.application.medicos.view;

import com.clinica.citas.application.catalogo.view.EspecialidadView;
import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.medicos.Medico;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Arma las vistas de medicos (con su especialidad) cargando todas las especialidades de una sola vez. */
@Component
public class MedicoViewFactory {

    private final EspecialidadRepository especialidades;

    public MedicoViewFactory(EspecialidadRepository especialidades) {
        this.especialidades = especialidades;
    }

    public MedicoView construir(Medico medico) {
        return construirLista(List.of(medico)).get(0);
    }

    public List<MedicoView> construirLista(List<Medico> medicos) {
        List<EspecialidadId> ids = medicos.stream().map(Medico::especialidadId).distinct().toList();
        Map<EspecialidadId, Especialidad> porId = especialidades.listarPorIds(ids).stream()
                .collect(Collectors.toMap(Especialidad::id, Function.identity()));
        return medicos.stream().map(m -> new MedicoView(
                m.id().valor(), m.nombre().nombres(), m.nombre().apellidos(), m.colegiatura().valor(), m.telefono(),
                m.correo() != null ? m.correo().valor() : null,
                porId.containsKey(m.especialidadId()) ? EspecialidadView.de(porId.get(m.especialidadId())) : null)).toList();
    }
}
