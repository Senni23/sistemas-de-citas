package com.clinica.citas.service;

import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Especialidad;
import com.clinica.citas.repository.EspecialidadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    public List<Especialidad> listar() {
        return especialidadRepository.findAll();
    }

    public Especialidad obtener(Long id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada: " + id));
    }

    public Especialidad crear(Especialidad especialidad) {
        return especialidadRepository.save(especialidad);
    }

    public Especialidad actualizar(Long id, Especialidad datos) {
        Especialidad especialidad = obtener(id);
        especialidad.setNombre(datos.getNombre());
        especialidad.setDescripcion(datos.getDescripcion());
        return especialidadRepository.save(especialidad);
    }

    public void eliminar(Long id) {
        Especialidad especialidad = obtener(id);
        especialidadRepository.delete(especialidad);
    }
}
