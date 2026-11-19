package com.clinica.citas.application.identidad;

import com.clinica.citas.application.identidad.view.SesionView;
import com.clinica.citas.domain.identidad.Rol;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import org.springframework.stereotype.Component;

/** Arma la respuesta de sesion: el token mas el nombre y el id del medico o paciente asociado. */
@Component
public class ConstructorDeSesion {

    private final EmisorDeTokens emisor;
    private final PacienteRepository pacientes;
    private final MedicoRepository medicos;

    public ConstructorDeSesion(EmisorDeTokens emisor, PacienteRepository pacientes, MedicoRepository medicos) {
        this.emisor = emisor;
        this.pacientes = pacientes;
        this.medicos = medicos;
    }

    public SesionView construir(Usuario usuario) {
        String token = emisor.emitir(usuario);
        Long entidadId = usuario.rol() == Rol.MEDICO && usuario.medicoId() != null ? usuario.medicoId().valor()
                : usuario.rol() == Rol.PACIENTE && usuario.pacienteId() != null ? usuario.pacienteId().valor()
                : null;
        String nombre = nombreDe(usuario);
        return new SesionView(token, usuario.username(), usuario.rol().name(), nombre, entidadId);
    }

    private String nombreDe(Usuario usuario) {
        if (usuario.medicoId() != null) {
            return medicos.buscarPorId(usuario.medicoId()).map(m -> m.nombre().completo()).orElse(usuario.username());
        }
        if (usuario.pacienteId() != null) {
            return pacientes.buscarPorId(usuario.pacienteId()).map(p -> p.nombre().completo()).orElse(usuario.username());
        }
        return usuario.username();
    }
}
