package com.clinica.citas.interfaces.rest.controller;

import com.clinica.citas.application.medicos.command.MedicoCommands;
import com.clinica.citas.application.medicos.handler.ActualizarMedicoHandler;
import com.clinica.citas.application.medicos.handler.EliminarMedicoHandler;
import com.clinica.citas.application.medicos.handler.ListarMedicosHandler;
import com.clinica.citas.application.medicos.handler.ObtenerMedicoHandler;
import com.clinica.citas.application.medicos.handler.RegistrarMedicoHandler;
import com.clinica.citas.application.medicos.query.MedicoQueries;
import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.interfaces.rest.request.MedicoActualizarRequest;
import com.clinica.citas.interfaces.rest.request.MedicoRegistroRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final ListarMedicosHandler listar;
    private final ObtenerMedicoHandler obtener;
    private final RegistrarMedicoHandler registrar;
    private final ActualizarMedicoHandler actualizar;
    private final EliminarMedicoHandler eliminar;

    public MedicoController(ListarMedicosHandler listar, ObtenerMedicoHandler obtener, RegistrarMedicoHandler registrar,
                            ActualizarMedicoHandler actualizar, EliminarMedicoHandler eliminar) {
        this.listar = listar;
        this.obtener = obtener;
        this.registrar = registrar;
        this.actualizar = actualizar;
        this.eliminar = eliminar;
    }

    @GetMapping
    public List<MedicoView> listar() {
        return listar.handle(new MedicoQueries.ListarTodos());
    }

    @GetMapping("/{id}")
    public MedicoView obtener(@PathVariable Long id) {
        return obtener.handle(new MedicoQueries.PorId(id));
    }

    @PostMapping
    public ResponseEntity<MedicoView> crear(@Valid @RequestBody MedicoRegistroRequest r) {
        MedicoView creado = registrar.handle(new MedicoCommands.Registrar(r.nombres(), r.apellidos(), r.colegiatura(),
                r.telefono(), r.email(), r.especialidadId(), r.password()));
        return RespuestasCreadas.creado(creado, creado.id());
    }

    @PutMapping("/{id}")
    public MedicoView actualizar(@PathVariable Long id, @Valid @RequestBody MedicoActualizarRequest r) {
        Long especialidadId = r.especialidad() != null ? r.especialidad().id() : null;
        return actualizar.handle(new MedicoCommands.Actualizar(id, r.nombres(), r.apellidos(), r.colegiatura(), r.telefono(),
                r.email(), especialidadId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        eliminar.handle(new MedicoCommands.Eliminar(id));
    }
}
