package com.clinica.citas.controller;

import com.clinica.citas.model.Servicio;
import com.clinica.citas.service.ServicioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public List<Servicio> listar(@RequestParam(required = false) Long especialidadId) {
        if (especialidadId != null) {
            return servicioService.listarPorEspecialidad(especialidadId);
        }
        return servicioService.listar();
    }
}
