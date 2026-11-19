package com.clinica.citas.application.citas.view;

import com.clinica.citas.application.catalogo.view.ServicioView;
import com.clinica.citas.application.catalogo.view.ServicioViewFactory;
import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.application.medicos.view.MedicoViewFactory;
import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.catalogo.ServicioRepository;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Arma las vistas de citas. Como la cita solo guarda ids, aqui se cargan en bloque (una consulta por tipo)
 * los pacientes, medicos y servicios que hacen falta, en vez de una consulta por cada cita.
 */
@Component
public class CitaViewFactory {

    private final PacienteRepository pacientes;
    private final MedicoRepository medicos;
    private final ServicioRepository servicios;
    private final MedicoViewFactory medicoVistas;
    private final ServicioViewFactory servicioVistas;

    public CitaViewFactory(PacienteRepository pacientes, MedicoRepository medicos, ServicioRepository servicios,
                           MedicoViewFactory medicoVistas, ServicioViewFactory servicioVistas) {
        this.pacientes = pacientes;
        this.medicos = medicos;
        this.servicios = servicios;
        this.medicoVistas = medicoVistas;
        this.servicioVistas = servicioVistas;
    }

    public CitaView construir(Cita cita) {
        return construirLista(List.of(cita)).get(0);
    }

    public List<CitaView> construirLista(List<Cita> citas) {
        Map<Long, PacienteView> pacientePorId = new HashMap<>();
        pacientes.listarPorIds(citas.stream().map(Cita::pacienteId).distinct().toList())
                .forEach(p -> pacientePorId.put(p.id().valor(), PacienteView.de(p)));

        Map<Long, MedicoView> medicoPorId = new HashMap<>();
        medicoVistas.construirLista(medicos.listarPorIds(citas.stream().map(Cita::medicoId).distinct().toList()))
                .forEach(m -> medicoPorId.put(m.id(), m));

        List<ServicioId> idsServicio = citas.stream().map(Cita::servicioId).filter(Objects::nonNull).distinct().toList();
        List<Servicio> serviciosEncontrados = servicios.listarPorIds(idsServicio);
        Map<Long, ServicioView> servicioPorId = new HashMap<>();
        servicioVistas.construirLista(serviciosEncontrados).forEach(s -> servicioPorId.put(s.id(), s));

        return citas.stream().map(c -> new CitaView(
                c.id() != null ? c.id().valor() : null,
                pacientePorId.get(c.pacienteId().valor()),
                medicoPorId.get(c.medicoId().valor()),
                c.servicioId() != null ? servicioPorId.get(c.servicioId().valor()) : null,
                c.horario().fecha(),
                c.horario().hora(),
                c.motivo(),
                c.estado(),
                c.estaPagada(),
                c.monto() != null ? c.monto().valor() : null,
                c.pago() != null ? c.pago().metodo() : null,
                c.pago() != null ? c.pago().fecha() : null)).toList();
    }
}
