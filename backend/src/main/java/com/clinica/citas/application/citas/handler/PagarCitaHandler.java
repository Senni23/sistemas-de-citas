package com.clinica.citas.application.citas.handler;

import com.clinica.citas.application.citas.command.CitaCommands;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.application.shared.CommandHandler;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioRepository;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/** Caso de uso: pagar una cita (pago simulado). La cita queda pagada y CONFIRMADA. */
@Service
public class PagarCitaHandler implements CommandHandler<CitaCommands.Pagar, CitaView> {

    private final CitaRepository citas;
    private final ServicioRepository servicios;
    private final Clock reloj;
    private final CitaViewFactory vistas;

    public PagarCitaHandler(CitaRepository citas, ServicioRepository servicios, Clock reloj, CitaViewFactory vistas) {
        this.citas = citas;
        this.servicios = servicios;
        this.reloj = reloj;
        this.vistas = vistas;
    }

    @Override
    @Transactional
    public CitaView handle(CitaCommands.Pagar c) {
        Cita cita = RecursoNoEncontradoException.requerir(citas.buscarPorId(new CitaId(c.id())), "Cita no encontrada: " + c.id());
        Dinero precio = cita.servicioId() == null ? null
                : servicios.buscarPorId(cita.servicioId()).map(Servicio::precio).orElse(null);
        cita.pagar(c.metodoPago(), LocalDateTime.now(reloj), precio);
        return vistas.construir(citas.guardar(cita));
    }
}
