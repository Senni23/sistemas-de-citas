package com.clinica.citas.controller;

import com.clinica.citas.model.Cita;
import com.clinica.citas.model.EstadoCita;
import com.clinica.citas.model.Paciente;
import com.clinica.citas.security.UsuarioPrincipal;
import com.clinica.citas.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<Cita> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
        exigirPersonalClinica(principal);
        return citaService.listar();
    }

    @GetMapping("/{id}")
    public Cita obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal) {
        Cita cita = citaService.obtener(id);
        exigirAccesoACita(principal, cita);
        return cita;
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<Cita> listarPorPaciente(@PathVariable Long pacienteId, @AuthenticationPrincipal UsuarioPrincipal principal) {
        if (esRol(principal, "PACIENTE") && !pacienteId.equals(principal.getPacienteId())) {
            throw new AccessDeniedException("No puedes ver las citas de otro paciente");
        }
        return citaService.listarPorPaciente(pacienteId);
    }

    @GetMapping("/medico/{medicoId}")
    public List<Cita> listarPorMedico(@PathVariable Long medicoId, @AuthenticationPrincipal UsuarioPrincipal principal) {
        if (esRol(principal, "MEDICO") && !medicoId.equals(principal.getMedicoId())) {
            throw new AccessDeniedException("No puedes ver la agenda de otro medico");
        }
        return citaService.listarPorMedico(medicoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cita crear(@Valid @RequestBody Cita cita, @AuthenticationPrincipal UsuarioPrincipal principal) {
        if (esRol(principal, "PACIENTE")) {
            // Un paciente solo puede agendar citas para si mismo, sin importar lo que mande el formulario.
            Paciente propio = new Paciente();
            propio.setId(principal.getPacienteId());
            cita.setPaciente(propio);
        }
        return citaService.crear(cita);
    }

    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable Long id, @Valid @RequestBody Cita cita, @AuthenticationPrincipal UsuarioPrincipal principal) {
        exigirPersonalClinica(principal);
        return citaService.actualizar(id, cita);
    }

    @PatchMapping("/{id}/estado")
    public Cita cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body,
                               @AuthenticationPrincipal UsuarioPrincipal principal) {
        Cita cita = citaService.obtener(id);
        EstadoCita nuevoEstado = EstadoCita.valueOf(body.get("estado"));

        if (esRol(principal, "PACIENTE")) {
            if (!Objects.equals(cita.getPaciente().getId(), principal.getPacienteId())) {
                throw new AccessDeniedException("No puedes modificar la cita de otro paciente");
            }
            if (nuevoEstado != EstadoCita.CANCELADA) {
                throw new AccessDeniedException("Solo puedes cancelar tu propia cita");
            }
        } else if (esRol(principal, "MEDICO")) {
            if (!Objects.equals(cita.getMedico().getId(), principal.getMedicoId())) {
                throw new AccessDeniedException("No puedes modificar la cita de otro medico");
            }
        }

        return citaService.cambiarEstado(id, nuevoEstado);
    }

    @PostMapping("/{id}/pagar")
    public Cita pagar(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body,
                       @AuthenticationPrincipal UsuarioPrincipal principal) {
        Cita cita = citaService.obtener(id);

        if (!esRol(principal, "PACIENTE") || !Objects.equals(cita.getPaciente().getId(), principal.getPacienteId())) {
            throw new AccessDeniedException("Solo el paciente dueno de la cita puede pagarla");
        }

        String metodoPago = (body != null && body.get("metodoPago") != null) ? body.get("metodoPago") : "TARJETA_SIMULADA";
        return citaService.pagar(id, metodoPago);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal) {
        exigirPersonalClinica(principal);
        citaService.eliminar(id);
    }

    private boolean esRol(UsuarioPrincipal principal, String rol) {
        return principal.getUsuario().getRol().name().equals(rol);
    }

    private void exigirPersonalClinica(UsuarioPrincipal principal) {
        if (esRol(principal, "PACIENTE") || esRol(principal, "MEDICO")) {
            throw new AccessDeniedException("Esta accion es solo para personal de la clinica");
        }
    }

    private void exigirAccesoACita(UsuarioPrincipal principal, Cita cita) {
        if (esRol(principal, "PACIENTE") && !Objects.equals(cita.getPaciente().getId(), principal.getPacienteId())) {
            throw new AccessDeniedException("No puedes ver la cita de otro paciente");
        }
        if (esRol(principal, "MEDICO") && !Objects.equals(cita.getMedico().getId(), principal.getMedicoId())) {
            throw new AccessDeniedException("No puedes ver la cita de otro medico");
        }
    }
}
