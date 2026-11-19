package com.clinica.citas.application.medicos.handler;

import com.clinica.citas.application.medicos.command.MedicoCommands;
import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.application.medicos.view.MedicoViewFactory;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.identidad.PasswordHasher;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import com.clinica.citas.domain.medicos.Colegiatura;
import com.clinica.citas.domain.medicos.Medico;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: dar de alta a un medico y crearle su cuenta para iniciar sesion. */
@Service
public class RegistrarMedicoHandler implements CommandHandler<MedicoCommands.Registrar, MedicoView> {

    private final MedicoRepository medicos;
    private final EspecialidadRepository especialidades;
    private final UsuarioRepository usuarios;
    private final PasswordHasher hasher;
    private final MedicoViewFactory vistas;

    public RegistrarMedicoHandler(MedicoRepository medicos, EspecialidadRepository especialidades,
                                  UsuarioRepository usuarios, PasswordHasher hasher, MedicoViewFactory vistas) {
        this.medicos = medicos;
        this.especialidades = especialidades;
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.vistas = vistas;
    }

    @Override
    @Transactional
    public MedicoView handle(MedicoCommands.Registrar c) {
        CorreoElectronico correo = CorreoElectronico.de(c.email());
        if (usuarios.buscarPorUsername(correo.valor()).isPresent()) {
            throw new RegistroDuplicadoException("Ya existe una cuenta con ese correo");
        }
        Especialidad especialidad = RecursoNoEncontradoException.requerir(
                especialidades.buscarPorId(new EspecialidadId(c.especialidadId())),
                "Especialidad no encontrada: " + c.especialidadId());

        Medico medico = medicos.guardar(Medico.registrar(new NombrePersona(c.nombres(), c.apellidos()),
                Colegiatura.de(c.colegiatura()), c.telefono(), correo, especialidad.id()));
        usuarios.guardar(Usuario.paraMedico(correo.valor(), hasher.cifrar(c.password()), medico.id()));
        return vistas.construir(medico);
    }
}
