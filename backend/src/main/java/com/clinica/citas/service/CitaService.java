package com.clinica.citas.service;

import com.clinica.citas.exception.HorarioInvalidoException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Cita;
import com.clinica.citas.model.EstadoCita;
import com.clinica.citas.model.Servicio;
import com.clinica.citas.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CitaService {

    private static final LocalTime TURNO_MANANA_INICIO = LocalTime.of(8, 0);
    private static final LocalTime TURNO_MANANA_FIN = LocalTime.of(14, 0);
    private static final LocalTime TURNO_TARDE_INICIO = LocalTime.of(16, 0);
    private static final LocalTime TURNO_TARDE_FIN = LocalTime.of(20, 0);

    private final CitaRepository citaRepository;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final ServicioService servicioService;

    public CitaService(CitaRepository citaRepository, PacienteService pacienteService, MedicoService medicoService,
                        ServicioService servicioService) {
        this.citaRepository = citaRepository;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.servicioService = servicioService;
    }

    public List<Cita> listar() {
        return citaRepository.findAll();
    }

    public Cita obtener(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada: " + id));
    }

    public List<Cita> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId);
    }

    public List<Cita> listarPorMedico(Long medicoId) {
        return citaRepository.findByMedicoId(medicoId);
    }

    public Cita crear(Cita cita) {
        validarHorario(cita.getHora());
        cita.setPaciente(pacienteService.obtener(cita.getPaciente().getId()));
        cita.setMedico(medicoService.obtener(cita.getMedico().getId()));
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setPagado(false);

        if (cita.getServicio() != null && cita.getServicio().getId() != null) {
            Servicio servicio = servicioService.obtener(cita.getServicio().getId());
            cita.setServicio(servicio);
            cita.setMontoPagado(servicio.getPrecio());
        }

        return citaRepository.save(cita);
    }

    public Cita actualizar(Long id, Cita datos) {
        validarHorario(datos.getHora());
        Cita cita = obtener(id);
        cita.setPaciente(pacienteService.obtener(datos.getPaciente().getId()));
        cita.setMedico(medicoService.obtener(datos.getMedico().getId()));
        cita.setFecha(datos.getFecha());
        cita.setHora(datos.getHora());
        cita.setMotivo(datos.getMotivo());
        return citaRepository.save(cita);
    }

    public Cita cambiarEstado(Long id, EstadoCita estado) {
        Cita cita = obtener(id);
        cita.setEstado(estado);
        return citaRepository.save(cita);
    }

    public Cita pagar(Long id, String metodoPago) {
        Cita cita = obtener(id);
        Servicio servicio = cita.getServicio();
        cita.setPagado(true);
        cita.setMetodoPago(metodoPago);
        cita.setFechaPago(LocalDateTime.now());
        cita.setMontoPagado(servicio != null ? servicio.getPrecio() : cita.getMontoPagado());
        cita.setEstado(EstadoCita.CONFIRMADA);
        return citaRepository.save(cita);
    }

    public void eliminar(Long id) {
        Cita cita = obtener(id);
        citaRepository.delete(cita);
    }

    private void validarHorario(LocalTime hora) {
        if (hora == null) {
            return;
        }
        boolean enTurnoManana = !hora.isBefore(TURNO_MANANA_INICIO) && !hora.isAfter(TURNO_MANANA_FIN);
        boolean enTurnoTarde = !hora.isBefore(TURNO_TARDE_INICIO) && !hora.isAfter(TURNO_TARDE_FIN);
        if (!enTurnoManana && !enTurnoTarde) {
            throw new HorarioInvalidoException(
                    "El horario de atencion es de 8:00 am a 2:00 pm y de 4:00 pm a 8:00 pm");
        }
    }
}
