package com.clinica.citas.application.citas.handler;

import com.clinica.citas.application.citas.command.CitaCommands;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CambiarEstadoCitaHandler implements CommandHandler<CitaCommands.CambiarEstado, CitaView> {

    private final CitaRepository citas;
    private final CitaViewFactory vistas;

    public CambiarEstadoCitaHandler(CitaRepository citas, CitaViewFactory vistas) {
        this.citas = citas;
        this.vistas = vistas;
    }

    @Override
    @Transactional
    public CitaView handle(CitaCommands.CambiarEstado c) {
        Cita cita = RecursoNoEncontradoException.requerir(citas.buscarPorId(new CitaId(c.id())), "Cita no encontrada: " + c.id());
        cita.cambiarEstado(c.estado());
        return vistas.construir(citas.guardar(cita));
    }
}
