package com.clinica.citas.service;

import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Especialidad;
import com.clinica.citas.model.Medico;
import com.clinica.citas.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadService especialidadService;

    public MedicoService(MedicoRepository medicoRepository, EspecialidadService especialidadService) {
        this.medicoRepository = medicoRepository;
        this.especialidadService = especialidadService;
    }

    public List<Medico> listar() {
        return medicoRepository.findAll();
    }

    public Medico obtener(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado: " + id));
    }

    public Medico crear(Medico medico) {
        Especialidad especialidad = especialidadService.obtener(medico.getEspecialidad().getId());
        medico.setEspecialidad(especialidad);
        return medicoRepository.save(medico);
    }

    public Medico actualizar(Long id, Medico datos) {
        Medico medico = obtener(id);
        medico.setNombres(datos.getNombres());
        medico.setApellidos(datos.getApellidos());
        medico.setColegiatura(datos.getColegiatura());
        medico.setTelefono(datos.getTelefono());
        medico.setEmail(datos.getEmail());
        if (datos.getEspecialidad() != null && datos.getEspecialidad().getId() != null) {
            medico.setEspecialidad(especialidadService.obtener(datos.getEspecialidad().getId()));
        }
        return medicoRepository.save(medico);
    }

    public void eliminar(Long id) {
        Medico medico = obtener(id);
        medicoRepository.delete(medico);
    }
}
