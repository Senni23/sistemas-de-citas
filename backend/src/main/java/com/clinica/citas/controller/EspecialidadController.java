package com.clinica.citas.controller;

import com.clinica.citas.model.Especialidad;
import com.clinica.citas.service.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @GetMapping
    public List<Especialidad> listar() {
        return especialidadService.listar();
    }

    @GetMapping("/{id}")
    public Especialidad obtener(@PathVariable Long id) {
        return especialidadService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Especialidad crear(@Valid @RequestBody Especialidad especialidad) {
        return especialidadService.crear(especialidad);
    }

    @PutMapping("/{id}")
    public Especialidad actualizar(@PathVariable Long id, @Valid @RequestBody Especialidad especialidad) {
        return especialidadService.actualizar(id, especialidad);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        especialidadService.eliminar(id);
    }
}
