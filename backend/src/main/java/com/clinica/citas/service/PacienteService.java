package com.clinica.citas.service;

import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Paciente;
import com.clinica.citas.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> listar() {
        return pacienteRepository.findAll();
    }

    public Paciente obtener(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado: " + id));
    }

    public Paciente crear(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Paciente actualizar(Long id, Paciente datos) {
        Paciente paciente = obtener(id);
        paciente.setNombres(datos.getNombres());
        paciente.setApellidos(datos.getApellidos());
        paciente.setDni(datos.getDni());
        paciente.setFechaNacimiento(datos.getFechaNacimiento());
        paciente.setTelefono(datos.getTelefono());
        paciente.setEmail(datos.getEmail());
        paciente.setDireccion(datos.getDireccion());
        return pacienteRepository.save(paciente);
    }

    public void eliminar(Long id) {
        Paciente paciente = obtener(id);
        pacienteRepository.delete(paciente);
    }
}
