package com.clinica.citas.controller;

import com.clinica.citas.repository.CitaRepository;
import com.clinica.citas.repository.MedicoRepository;
import com.clinica.citas.repository.PacienteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;

    public DashboardController(PacienteRepository pacienteRepository, MedicoRepository medicoRepository,
                                CitaRepository citaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
    }

    @GetMapping("/resumen")
    public Map<String, Object> resumen() {
        long citasHoy = citaRepository.findAll().stream()
                .filter(c -> LocalDate.now().equals(c.getFecha()))
                .count();
        return Map.of(
                "totalPacientes", pacienteRepository.count(),
                "totalMedicos", medicoRepository.count(),
                "totalCitas", citaRepository.count(),
                "citasHoy", citasHoy
        );
    }
}
