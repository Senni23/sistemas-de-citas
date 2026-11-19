package com.clinica.citas.application.medicos.handler;

import com.clinica.citas.application.medicos.command.MedicoCommands;
import com.clinica.citas.application.medicos.view.MedicoView;
import com.clinica.citas.application.medicos.view.MedicoViewFactory;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.medicos.Colegiatura;
import com.clinica.citas.domain.medicos.Medico;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarMedicoHandler implements CommandHandler<MedicoCommands.Actualizar, MedicoView> {

    private final MedicoRepository medicos;
    private final EspecialidadRepository especialidades;
    private final MedicoViewFactory vistas;

    public ActualizarMedicoHandler(MedicoRepository medicos, EspecialidadRepository especialidades, MedicoViewFactory vistas) {
        this.medicos = medicos;
        this.especialidades = especialidades;
        this.vistas = vistas;
    }

    @Override
    @Transactional
    public MedicoView handle(MedicoCommands.Actualizar c) {
        Medico medico = RecursoNoEncontradoException.requerir(
                medicos.buscarPorId(new MedicoId(c.id())), "Medico no encontrado: " + c.id());
        EspecialidadId nuevaEspecialidad = null;
        if (c.especialidadId() != null) {
            nuevaEspecialidad = RecursoNoEncontradoException.requerir(
                    especialidades.buscarPorId(new EspecialidadId(c.especialidadId())),
                    "Especialidad no encontrada: " + c.especialidadId()).id();
        }
        medico.actualizarDatos(new NombrePersona(c.nombres(), c.apellidos()), Colegiatura.de(c.colegiatura()),
                c.telefono(), CorreoElectronico.opcional(c.email()), nuevaEspecialidad);
        return vistas.construir(medicos.guardar(medico));
    }
}
