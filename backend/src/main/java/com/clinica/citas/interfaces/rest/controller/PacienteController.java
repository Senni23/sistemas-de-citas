package com.clinica.citas.interfaces.rest.controller;

import com.clinica.citas.application.pacientes.command.PacienteCommands;
import com.clinica.citas.application.pacientes.handler.ActualizarPacienteHandler;
import com.clinica.citas.application.pacientes.handler.EliminarPacienteHandler;
import com.clinica.citas.application.pacientes.handler.ListarPacientesHandler;
import com.clinica.citas.application.pacientes.handler.ObtenerPacienteHandler;
import com.clinica.citas.application.pacientes.handler.RegistrarPacienteHandler;
import com.clinica.citas.application.pacientes.query.PacienteQueries;
import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.interfaces.rest.request.PacienteRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final ListarPacientesHandler listar;
    private final ObtenerPacienteHandler obtener;
    private final RegistrarPacienteHandler registrar;
    private final ActualizarPacienteHandler actualizar;
    private final EliminarPacienteHandler eliminar;

    public PacienteController(ListarPacientesHandler listar, ObtenerPacienteHandler obtener, RegistrarPacienteHandler registrar,
                              ActualizarPacienteHandler actualizar, EliminarPacienteHandler eliminar) {
        this.listar = listar;
        this.obtener = obtener;
        this.registrar = registrar;
        this.actualizar = actualizar;
        this.eliminar = eliminar;
    }

    @GetMapping
    public List<PacienteView> listar() {
        return listar.handle(new PacienteQueries.ListarTodos());
    }

    @GetMapping("/{id}")
    public PacienteView obtener(@PathVariable Long id) {
        return obtener.handle(new PacienteQueries.PorId(id));
    }

    @PostMapping
    public ResponseEntity<PacienteView> crear(@Valid @RequestBody PacienteRequest r) {
        PacienteView creado = registrar.handle(new PacienteCommands.Registrar(r.nombres(), r.apellidos(), r.dni(),
                r.fechaNacimiento(), r.telefono(), r.email(), r.direccion()));
        return RespuestasCreadas.creado(creado, creado.id());
    }

    @PutMapping("/{id}")
    public PacienteView actualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequest r) {
        return actualizar.handle(new PacienteCommands.Actualizar(id, r.nombres(), r.apellidos(), r.dni(), r.fechaNacimiento(),
                r.telefono(), r.email(), r.direccion()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        eliminar.handle(new PacienteCommands.Eliminar(id));
    }
}
