package com.clinica.citas.application.dashboard.query;

import com.clinica.citas.application.dashboard.view.ResumenDashboardView;
import com.clinica.citas.application.shared.Query;

public final class DashboardQueries {

    private DashboardQueries() {
    }

    public record ObtenerResumen() implements Query<ResumenDashboardView> {
    }
}
