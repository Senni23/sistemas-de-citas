package com.clinica.citas.interfaces.rest.controller;

import com.clinica.citas.application.citas.command.CitaCommands;
import com.clinica.citas.application.citas.handler.AgendarCitaHandler;
import com.clinica.citas.application.citas.handler.BuscarCitasHandler;
import com.clinica.citas.application.citas.handler.CambiarEstadoCitaHandler;
import com.clinica.citas.application.citas.handler.EliminarCitaHandler;
import com.clinica.citas.application.citas.handler.ListarCitasHandler;
import com.clinica.citas.application.citas.handler.ListarCitasPorMedicoHandler;
import com.clinica.citas.application.citas.handler.ListarCitasPorPacienteHandler;
import com.clinica.citas.application.citas.handler.ObtenerCitaHandler;
import com.clinica.citas.application.citas.handler.PagarCitaHandler;
import com.clinica.citas.application.citas.handler.ReprogramarCitaHandler;
import com.clinica.citas.application.citas.query.CitaQueries;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.shared.PaginaView;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.OrdenDeCitas;
import com.clinica.citas.domain.identidad.Rol;
import com.clinica.citas.infrastructure.security.UsuarioPrincipal;
import com.clinica.citas.interfaces.rest.request.CitaRequest;
import com.clinica.citas.interfaces.rest.request.EstadoRequest;
import com.clinica.citas.interfaces.rest.request.PagoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Citas: cada rol solo puede ver y modificar lo suyo. El paciente solo agenda para si mismo, el medico solo
 * ve su agenda y el personal de la clinica puede gestionar todo. Las reglas del negocio las aplican los handlers.
 */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private static final String METODO_DE_PAGO_POR_DEFECTO = "TARJETA_SIMULADA";

    private final ListarCitasHandler listar;
    private final BuscarCitasHandler buscar;
    private final ObtenerCitaHandler obtener;
    private final ListarCitasPorPacienteHandler listarPorPaciente;
    private final ListarCitasPorMedicoHandler listarPorMedico;
    private final AgendarCitaHandler agendar;
    private final ReprogramarCitaHandler reprogramar;
    private final CambiarEstadoCitaHandler cambiarEstado;
    private final PagarCitaHandler pagar;
    private final EliminarCitaHandler eliminar;

    public CitaController(ListarCitasHandler listar, BuscarCitasHandler buscar, ObtenerCitaHandler obtener,
                          ListarCitasPorPacienteHandler listarPorPaciente, ListarCitasPorMedicoHandler listarPorMedico,
                          AgendarCitaHandler agendar, ReprogramarCitaHandler reprogramar,
                          CambiarEstadoCitaHandler cambiarEstado, PagarCitaHandler pagar, EliminarCitaHandler eliminar) {
        this.listar = listar;
        this.buscar = buscar;
        this.obtener = obtener;
        this.listarPorPaciente = listarPorPaciente;
        this.listarPorMedico = listarPorMedico;
        this.agendar = agendar;
        this.reprogramar = reprogramar;
        this.cambiarEstado = cambiarEstado;
        this.pagar = pagar;
        this.eliminar = eliminar;
    }

    @GetMapping
    public List<CitaView> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
        exigirPersonalClinica(principal);
        return listar.handle(new CitaQueries.ListarTodas());
    }

    /**
     * Listado de a una pagina, para el personal de la clinica. Ejemplo:
     * {@code GET /api/citas/buscar?estado=PENDIENTE&orden=FECHA&descendente=true&pagina=0&tamano=10}.
     * Todos los parametros son opcionales; {@code orden} puede ser FECHA, ESTADO o ID.
     */
    @GetMapping("/buscar")
    public PaginaView<CitaView> buscar(@RequestParam(required = false) EstadoCita estado,
                                       @RequestParam(defaultValue = "FECHA") OrdenDeCitas orden,
                                       @RequestParam(defaultValue = "false") boolean descendente,
                                       @RequestParam(defaultValue = "0") int pagina,
                                       @RequestParam(defaultValue = "10") int tamano,
                                       @AuthenticationPrincipal UsuarioPrincipal principal) {
        exigirPersonalClinica(principal);
        return buscar.handle(new CitaQueries.Buscar(estado, orden, descendente, pagina, tamano));
    }

    @GetMapping("/{id}")
    public CitaView obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal) {
        CitaView cita = obtener.handle(new CitaQueries.PorId(id));
        exigirAccesoACita(principal, cita);
        return cita;
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<CitaView> listarPorPaciente(@PathVariable Long pacienteId, @AuthenticationPrincipal UsuarioPrincipal principal) {
        if (esRol(principal, Rol.PACIENTE) && !pacienteId.equals(principal.getPacienteId())) {
            throw new AccessDeniedException("No puedes ver las citas de otro paciente");
        }
        return listarPorPaciente.handle(new CitaQueries.PorPaciente(pacienteId));
    }

    @GetMapping("/medico/{medicoId}")
    public List<CitaView> listarPorMedico(@PathVariable Long medicoId, @AuthenticationPrincipal UsuarioPrincipal principal) {
        if (esRol(principal, Rol.MEDICO) && !medicoId.equals(principal.getMedicoId())) {
            throw new AccessDeniedException("No puedes ver la agenda de otro medico");
        }
        return listarPorMedico.handle(new CitaQueries.PorMedico(medicoId));
    }

    @PostMapping
    public ResponseEntity<CitaView> crear(@Valid @RequestBody CitaRequest r, @AuthenticationPrincipal UsuarioPrincipal principal) {
        // Un paciente solo puede agendar citas para si mismo, sin importar lo que mande el formulario.
        Long pacienteId = esRol(principal, Rol.PACIENTE) ? principal.getPacienteId() : r.paciente().id();
        Long servicioId = r.servicio() != null ? r.servicio().id() : null;
        CitaView creada = agendar.handle(new CitaCommands.Agendar(pacienteId, r.medico().id(), servicioId, r.fecha(), r.hora(), r.motivo()));
        return RespuestasCreadas.creado(creada, creada.id());
    }

    @PutMapping("/{id}")
    public CitaView actualizar(@PathVariable Long id, @Valid @RequestBody CitaRequest r,
                               @AuthenticationPrincipal UsuarioPrincipal principal) {
        exigirPersonalClinica(principal);
        return reprogramar.handle(new CitaCommands.Reprogramar(id, r.paciente().id(), r.medico().id(), r.fecha(), r.hora(), r.motivo()));
    }

    @PatchMapping("/{id}/estado")
    public CitaView cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest body,
                                  @AuthenticationPrincipal UsuarioPrincipal principal) {
        CitaView cita = obtener.handle(new CitaQueries.PorId(id));
        EstadoCita nuevoEstado = EstadoCita.valueOf(body.estado());

        if (esRol(principal, Rol.PACIENTE)) {
            if (!Objects.equals(cita.paciente().id(), principal.getPacienteId())) {
                throw new AccessDeniedException("No puedes modificar la cita de otro paciente");
            }
            if (nuevoEstado != EstadoCita.CANCELADA) {
                throw new AccessDeniedException("Solo puedes cancelar tu propia cita");
            }
        } else if (esRol(principal, Rol.MEDICO)) {
            if (!Objects.equals(cita.medico().id(), principal.getMedicoId())) {
                throw new AccessDeniedException("No puedes modificar la cita de otro medico");
            }
        }

        return cambiarEstado.handle(new CitaCommands.CambiarEstado(id, nuevoEstado));
    }

    @PostMapping("/{id}/pagar")
    public CitaView pagar(@PathVariable Long id, @RequestBody(required = false) PagoRequest body,
                          @AuthenticationPrincipal UsuarioPrincipal principal) {
        CitaView cita = obtener.handle(new CitaQueries.PorId(id));

        if (!esRol(principal, Rol.PACIENTE) || !Objects.equals(cita.paciente().id(), principal.getPacienteId())) {
            throw new AccessDeniedException("Solo el paciente dueno de la cita puede pagarla");
        }

        String metodoPago = body != null && body.metodoPago() != null ? body.metodoPago() : METODO_DE_PAGO_POR_DEFECTO;
        return pagar.handle(new CitaCommands.Pagar(id, metodoPago));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal) {
        exigirPersonalClinica(principal);
        eliminar.handle(new CitaCommands.Eliminar(id));
    }

    private boolean esRol(UsuarioPrincipal principal, Rol rol) {
        return principal.getUsuario().rol() == rol;
    }

    private void exigirPersonalClinica(UsuarioPrincipal principal) {
        if (!principal.getUsuario().rol().esPersonalClinica()) {
            throw new AccessDeniedException("Esta accion es solo para personal de la clinica");
        }
    }

    private void exigirAccesoACita(UsuarioPrincipal principal, CitaView cita) {
        if (esRol(principal, Rol.PACIENTE) && !Objects.equals(cita.paciente().id(), principal.getPacienteId())) {
            throw new AccessDeniedException("No puedes ver la cita de otro paciente");
        }
        if (esRol(principal, Rol.MEDICO) && !Objects.equals(cita.medico().id(), principal.getMedicoId())) {
            throw new AccessDeniedException("No puedes ver la cita de otro medico");
        }
    }
}
