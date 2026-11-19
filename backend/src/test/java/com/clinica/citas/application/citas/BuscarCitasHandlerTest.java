package com.clinica.citas.application.citas;

import com.clinica.citas.application.citas.handler.BuscarCitasHandler;
import com.clinica.citas.application.citas.query.CitaQueries;
import com.clinica.citas.application.citas.view.CitaView;
import com.clinica.citas.application.citas.view.CitaViewFactory;
import com.clinica.citas.application.shared.PaginaView;
import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.citas.OrdenDeCitas;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.DomainException;
import com.clinica.citas.domain.shared.Pagina;
import com.clinica.citas.domain.shared.SolicitudDePagina;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarCitasHandlerTest {

    @Mock
    private CitaRepository citas;
    @Mock
    private CitaViewFactory vistas;

    private Cita cita() {
        return Cita.reconstruir(new CitaId(9L), new PacienteId(1L), new MedicoId(2L), new ServicioId(3L),
                HorarioCita.reconstruir(LocalDate.of(2026, 12, 15), LocalTime.of(10, 0)), "Control",
                EstadoCita.PENDIENTE, Dinero.de("50"), null);
    }

    private CitaView vista() {
        return new CitaView(9L, null, null, null, LocalDate.of(2026, 12, 15), LocalTime.of(10, 0), "Control",
                EstadoCita.PENDIENTE, false, null, null, null);
    }

    @Test
    @DisplayName("Devuelve la pagina con las vistas armadas y los totales que dio el repositorio")
    void devuelveLaPaginaConVistas() {
        Cita cita = cita();
        SolicitudDePagina solicitud = new SolicitudDePagina(1, 5);
        when(citas.buscarPagina(EstadoCita.PENDIENTE, OrdenDeCitas.FECHA, true, solicitud))
                .thenReturn(new Pagina<>(List.of(cita), 1, 5, 11));
        when(vistas.construirLista(List.of(cita))).thenReturn(List.of(vista()));

        PaginaView<CitaView> respuesta = new BuscarCitasHandler(citas, vistas)
                .handle(new CitaQueries.Buscar(EstadoCita.PENDIENTE, OrdenDeCitas.FECHA, true, 1, 5));

        assertThat(respuesta.contenido()).containsExactly(vista());
        assertThat(respuesta.pagina()).isEqualTo(1);
        assertThat(respuesta.tamano()).isEqualTo(5);
        assertThat(respuesta.totalElementos()).isEqualTo(11);
        assertThat(respuesta.totalPaginas()).isEqualTo(3);
    }

    @Test
    @DisplayName("Un tamano de pagina fuera de rango se rechaza antes de consultar la base")
    void rechazaTamanoInvalido() {
        BuscarCitasHandler handler = new BuscarCitasHandler(citas, vistas);

        assertThatThrownBy(() -> handler.handle(new CitaQueries.Buscar(null, OrdenDeCitas.ID, false, 0, 500)))
                .isInstanceOf(DomainException.class);

        verify(citas, never()).buscarPagina(any(), any(), org.mockito.ArgumentMatchers.anyBoolean(), any());
    }
}
