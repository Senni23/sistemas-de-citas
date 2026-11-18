package com.clinica.citas.application.citas.handler;

import com.clinica.citas.application.citas.query.CitaQueries;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.application.shared.PaginaView;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.shared.Pagina;
import com.clinica.citas.domain.shared.SolicitudDePagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta: citas de a una pagina, con filtro por estado y orden elegido. */
@Service
public class BuscarCitasHandler implements QueryHandler<CitaQueries.Buscar, PaginaView<CitaView>> {

    private final CitaRepository citas;
    private final CitaViewFactory vistas;

    public BuscarCitasHandler(CitaRepository citas, CitaViewFactory vistas) {
        this.citas = citas;
        this.vistas = vistas;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaView<CitaView> handle(CitaQueries.Buscar query) {
        // Los limites (pagina >= 0, tamano entre 1 y 100) los valida el objeto de valor.
        SolicitudDePagina solicitud = new SolicitudDePagina(query.pagina(), query.tamano());
        Pagina<Cita> encontradas = citas.buscarPagina(query.estado(), query.orden(), query.descendente(), solicitud);
        return PaginaView.de(encontradas.conContenido(vistas.construirLista(encontradas.contenido())));
    }
}
