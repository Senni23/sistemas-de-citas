package com.clinica.citas.interfaces.rest.controller;

import com.clinica.citas.application.catalogo.handler.ListarServiciosHandler;
import com.clinica.citas.application.catalogo.query.CatalogoQueries;
import com.clinica.citas.application.catalogo.view.ServicioView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ListarServiciosHandler listar;

    public ServicioController(ListarServiciosHandler listar) {
        this.listar = listar;
    }

    @GetMapping
    public List<ServicioView> listar(@RequestParam(required = false) Long especialidadId) {
        return listar.handle(new CatalogoQueries.ListarServicios(especialidadId));
    }
}
