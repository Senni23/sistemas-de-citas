package com.clinica.citas.service;

import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Servicio;
import com.clinica.citas.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<Servicio> listar() {
        return servicioRepository.findAll();
    }

    public List<Servicio> listarPorEspecialidad(Long especialidadId) {
        return servicioRepository.findByEspecialidadId(especialidadId);
    }

    public Servicio obtener(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + id));
    }
}
