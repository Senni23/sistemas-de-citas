package com.clinica.citas.controller;

import com.clinica.citas.model.Medico;
import com.clinica.citas.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    public List<Medico> listar() {
        return medicoService.listar();
    }

    @GetMapping("/{id}")
    public Medico obtener(@PathVariable Long id) {
        return medicoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medico crear(@Valid @RequestBody Medico medico) {
        return medicoService.crear(medico);
    }

    @PutMapping("/{id}")
    public Medico actualizar(@PathVariable Long id, @Valid @RequestBody Medico medico) {
        return medicoService.actualizar(id, medico);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        medicoService.eliminar(id);
    }
}
