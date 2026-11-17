package com.clinica.citas.interfaces.rest.controller;

import com.clinica.citas.application.dashboard.handler.ObtenerResumenDashboardHandler;
import com.clinica.citas.application.dashboard.query.DashboardQueries;
import com.clinica.citas.application.dashboard.view.ResumenDashboardView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ObtenerResumenDashboardHandler resumen;

    public DashboardController(ObtenerResumenDashboardHandler resumen) {
        this.resumen = resumen;
    }

    @GetMapping("/resumen")
    public ResumenDashboardView resumen() {
        return resumen.handle(new DashboardQueries.ObtenerResumen());
    }
}
