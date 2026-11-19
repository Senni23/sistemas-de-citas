package com.clinica.citas.application.citas.handler;

import com.clinica.citas.application.citas.command.CitaCommands;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.DisponibilidadMedico;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: reprogramar una cita (la propia cita no cuenta como cruce consigo misma). */
@Service
public class ReprogramarCitaHandler implements CommandHandler<CitaCommands.Reprogramar, CitaView> {

    private final CitaRepository citas;
    private final PacienteRepository pacientes;
    private final MedicoRepository medicos;
    private final DisponibilidadMedico disponibilidad;
    private final CitaViewFactory vistas;

    public ReprogramarCitaHandler(CitaRepository citas, PacienteRepository pacientes, MedicoRepository medicos,
                                  DisponibilidadMedico disponibilidad, CitaViewFactory vistas) {
        this.citas = citas;
        this.pacientes = pacientes;
        this.medicos = medicos;
        this.disponibilidad = disponibilidad;
        this.vistas = vistas;
    }

    @Override
    @Transactional
    public CitaView handle(CitaCommands.Reprogramar c) {
        HorarioCita horario = HorarioCita.programar(c.fecha(), c.hora());
        CitaId citaId = new CitaId(c.id());
        PacienteId pacienteId = new PacienteId(c.pacienteId());
        MedicoId medicoId = new MedicoId(c.medicoId());

        disponibilidad.exigirLibre(medicoId, horario, citaId);
        Cita cita = RecursoNoEncontradoException.requerir(citas.buscarPorId(citaId), "Cita no encontrada: " + c.id());
        RecursoNoEncontradoException.requerir(pacientes.buscarPorId(pacienteId), "Paciente no encontrado: " + c.pacienteId());
        RecursoNoEncontradoException.requerir(medicos.buscarPorId(medicoId), "Medico no encontrado: " + c.medicoId());

        cita.reprogramar(pacienteId, medicoId, horario, c.motivo());
        return vistas.construir(citas.guardar(cita));
    }
}
