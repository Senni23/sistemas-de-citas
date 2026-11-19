package com.clinica.citas.application.identidad.handler;

import com.clinica.citas.application.identidad.ConstructorDeSesion;
import com.clinica.citas.application.identidad.command.IdentidadCommands;
import com.clinica.citas.application.identidad.view.SesionView;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.identidad.PasswordHasher;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: un paciente se registra por su cuenta (usuario = su correo, contrasena cifrada). */
@Service
public class RegistrarPacienteConCuentaHandler implements CommandHandler<IdentidadCommands.RegistrarPacienteConCuenta, SesionView> {

    private final PacienteRepository pacientes;
    private final UsuarioRepository usuarios;
    private final PasswordHasher hasher;
    private final ConstructorDeSesion sesiones;

    public RegistrarPacienteConCuentaHandler(PacienteRepository pacientes, UsuarioRepository usuarios,
                                             PasswordHasher hasher, ConstructorDeSesion sesiones) {
        this.pacientes = pacientes;
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.sesiones = sesiones;
    }

    @Override
    @Transactional
    public SesionView handle(IdentidadCommands.RegistrarPacienteConCuenta c) {
        Dni dni = Dni.de(c.dni());
        CorreoElectronico correo = CorreoElectronico.de(c.email());

        if (pacientes.buscarPorDni(dni).isPresent()) {
            throw new RegistroDuplicadoException("Ya existe un paciente registrado con ese DNI");
        }
        if (usuarios.buscarPorUsername(correo.valor()).isPresent()) {
            throw new RegistroDuplicadoException("Ya existe una cuenta con ese correo");
        }

        Paciente paciente = pacientes.guardar(Paciente.registrar(new NombrePersona(c.nombres(), c.apellidos()),
                dni, null, c.telefono(), correo, c.direccion()));
        Usuario usuario = usuarios.guardar(Usuario.paraPaciente(correo.valor(), hasher.cifrar(c.password()), paciente.id()));
        return sesiones.construir(usuario);
    }
}
