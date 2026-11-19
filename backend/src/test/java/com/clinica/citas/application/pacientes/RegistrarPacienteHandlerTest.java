package com.clinica.citas.application.pacientes;

import com.clinica.citas.application.pacientes.command.PacienteCommands;
import com.clinica.citas.application.pacientes.handler.RegistrarPacienteHandler;
import com.clinica.citas.application.pacientes.view.PacienteView;
import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.DomainException;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegistrarPacienteHandlerTest {

    @Mock
    private PacienteRepository pacientes;

    private PacienteCommands.Registrar comando(String email) {
        return new PacienteCommands.Registrar("Ana", "Lopez", "71234567", LocalDate.of(1990, 5, 1), "999", email, "Lima");
    }

    @Test
    @DisplayName("Registrar un paciente nuevo lo guarda y devuelve su vista")
    void registrar_guardaYDevuelveLaVista() {
        when(pacientes.buscarPorDni(any())).thenReturn(Optional.empty());
        when(pacientes.guardar(any())).thenReturn(Paciente.reconstruir(new PacienteId(5L), new NombrePersona("Ana", "Lopez"),
                Dni.reconstruir("71234567"), LocalDate.of(1990, 5, 1), "999", null, "Lima"));

        PacienteView vista = new RegistrarPacienteHandler(pacientes).handle(comando("ana@correo.com"));

        assertThat(vista.id()).isEqualTo(5L);
        assertThat(vista.nombres()).isEqualTo("Ana");
        assertThat(vista.dni()).isEqualTo("71234567");
    }

    @Test
    @DisplayName("No se registran dos pacientes con el mismo DNI")
    void registrar_dniDuplicado_lanzaRegistroDuplicado() {
        when(pacientes.buscarPorDni(any())).thenReturn(Optional.of(Paciente.reconstruir(new PacienteId(1L),
                new NombrePersona("Otro", "Paciente"), Dni.reconstruir("71234567"), null, null, null, null)));

        assertThatThrownBy(() -> new RegistrarPacienteHandler(pacientes).handle(comando(null)))
                .isInstanceOf(RegistroDuplicadoException.class);
        verify(pacientes, never()).guardar(any());
    }

    @Test
    @DisplayName("Un correo con formato invalido es rechazado por el dominio")
    void registrar_correoInvalido_lanzaError() {
        when(pacientes.buscarPorDni(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new RegistrarPacienteHandler(pacientes).handle(comando("no-es-correo")))
                .isInstanceOf(DomainException.class);
    }
}
