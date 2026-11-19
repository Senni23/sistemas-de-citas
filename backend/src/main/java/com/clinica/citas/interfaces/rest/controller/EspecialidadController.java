package com.clinica.citas.interfaces.rest.controller;

import com.clinica.citas.application.catalogo.command.EspecialidadCommands;
import com.clinica.citas.application.catalogo.handler.ActualizarEspecialidadHandler;
import com.clinica.citas.application.catalogo.handler.CrearEspecialidadHandler;
import com.clinica.citas.application.catalogo.handler.EliminarEspecialidadHandler;
import com.clinica.citas.application.catalogo.handler.ListarEspecialidadesHandler;
import com.clinica.citas.application.catalogo.handler.ObtenerEspecialidadHandler;
import com.clinica.citas.application.catalogo.query.CatalogoQueries;
import com.clinica.citas.application.catalogo.view.EspecialidadView;
import com.clinica.citas.interfaces.rest.request.EspecialidadRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final ListarEspecialidadesHandler listar;
    private final ObtenerEspecialidadHandler obtener;
    private final CrearEspecialidadHandler crear;
    private final ActualizarEspecialidadHandler actualizar;
    private final EliminarEspecialidadHandler eliminar;

    public EspecialidadController(ListarEspecialidadesHandler listar, ObtenerEspecialidadHandler obtener,
                                  CrearEspecialidadHandler crear, ActualizarEspecialidadHandler actualizar,
                                  EliminarEspecialidadHandler eliminar) {
        this.listar = listar;
        this.obtener = obtener;
        this.crear = crear;
        this.actualizar = actualizar;
        this.eliminar = eliminar;
    }

    @GetMapping
    public List<EspecialidadView> listar() {
        return listar.handle(new CatalogoQueries.ListarEspecialidades());
    }

    @GetMapping("/{id}")
    public EspecialidadView obtener(@PathVariable Long id) {
        return obtener.handle(new CatalogoQueries.ObtenerEspecialidad(id));
    }

    @PostMapping
    public ResponseEntity<EspecialidadView> crear(@Valid @RequestBody EspecialidadRequest r) {
        EspecialidadView creada = crear.handle(new EspecialidadCommands.Crear(r.nombre(), r.descripcion()));
        return RespuestasCreadas.creado(creada, creada.id());
    }

    @PutMapping("/{id}")
    public EspecialidadView actualizar(@PathVariable Long id, @Valid @RequestBody EspecialidadRequest r) {
        return actualizar.handle(new EspecialidadCommands.Actualizar(id, r.nombre(), r.descripcion()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        eliminar.handle(new EspecialidadCommands.Eliminar(id));
    }
}
