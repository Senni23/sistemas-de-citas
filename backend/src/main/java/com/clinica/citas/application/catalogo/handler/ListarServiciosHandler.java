package com.clinica.citas.application.catalogo.handler;

import com.clinica.citas.application.catalogo.query.CatalogoQueries;
import com.clinica.citas.application.catalogo.view.ServicioView;
import com.clinica.citas.application.catalogo.view.ServicioViewFactory;
import com.clinica.citas.application.shared.QueryHandler;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarServiciosHandler implements QueryHandler<CatalogoQueries.ListarServicios, List<ServicioView>> {

    private final ServicioRepository servicios;
    private final ServicioViewFactory vistas;

    public ListarServiciosHandler(ServicioRepository servicios, ServicioViewFactory vistas) {
        this.servicios = servicios;
        this.vistas = vistas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioView> handle(CatalogoQueries.ListarServicios query) {
        List<Servicio> lista = query.especialidadId() != null
                ? servicios.listarPorEspecialidad(new EspecialidadId(query.especialidadId()))
                : servicios.listar();
        return vistas.construirLista(lista);
    }
}
