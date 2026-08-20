package com.clinica.citas.service;

import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Cita;
import com.clinica.citas.model.EstadoCita;
import com.clinica.citas.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    public CitaService(CitaRepository citaRepository, PacienteService pacienteService, MedicoService medicoService) {
        this.citaRepository = citaRepository;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
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

    public Cita crear(Cita cita) {
        cita.setPaciente(pacienteService.obtener(cita.getPaciente().getId()));
        cita.setMedico(medicoService.obtener(cita.getMedico().getId()));
        cita.setEstado(EstadoCita.PENDIENTE);
        return citaRepository.save(cita);
    }

    public Cita actualizar(Long id, Cita datos) {
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

    public void eliminar(Long id) {
        Cita cita = obtener(id);
        citaRepository.delete(cita);
    }
}
