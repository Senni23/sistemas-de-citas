package com.clinica.citas.application.citas.handler;

import com.clinica.citas.application.citas.command.CitaCommands;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.catalogo.ServicioRepository;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.DisponibilidadMedico;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: agendar una cita. El dominio valida el horario de atencion y que el medico este libre;
 * el servicio elegido aporta el monto.
 */
@Service
public class AgendarCitaHandler implements CommandHandler<CitaCommands.Agendar, CitaView> {

    private final CitaRepository citas;
    private final PacienteRepository pacientes;
    private final MedicoRepository medicos;
    private final ServicioRepository servicios;
    private final DisponibilidadMedico disponibilidad;
    private final CitaViewFactory vistas;

    public AgendarCitaHandler(CitaRepository citas, PacienteRepository pacientes, MedicoRepository medicos,
                              ServicioRepository servicios, DisponibilidadMedico disponibilidad, CitaViewFactory vistas) {
        this.citas = citas;
        this.pacientes = pacientes;
        this.medicos = medicos;
        this.servicios = servicios;
        this.disponibilidad = disponibilidad;
        this.vistas = vistas;
    }

    @Override
    @Transactional
    public CitaView handle(CitaCommands.Agendar c) {
        HorarioCita horario = HorarioCita.programar(c.fecha(), c.hora());
        PacienteId pacienteId = new PacienteId(c.pacienteId());
        MedicoId medicoId = new MedicoId(c.medicoId());

        disponibilidad.exigirLibre(medicoId, horario, null);
        RecursoNoEncontradoException.requerir(pacientes.buscarPorId(pacienteId), "Paciente no encontrado: " + c.pacienteId());
        RecursoNoEncontradoException.requerir(medicos.buscarPorId(medicoId), "Medico no encontrado: " + c.medicoId());

        ServicioId servicioId = null;
        Dinero monto = null;
        if (c.servicioId() != null) {
            Servicio servicio = RecursoNoEncontradoException.requerir(
                    servicios.buscarPorId(new ServicioId(c.servicioId())), "Servicio no encontrado: " + c.servicioId());
            servicioId = servicio.id();
            monto = servicio.precio();
        }

        Cita cita = Cita.agendar(pacienteId, medicoId, servicioId, monto, horario, c.motivo());
        return vistas.construir(citas.guardar(cita));
    }
}
