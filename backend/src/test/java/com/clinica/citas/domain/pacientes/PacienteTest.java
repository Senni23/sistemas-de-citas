package com.clinica.citas.domain.pacientes;

import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.DomainException;
import com.clinica.citas.domain.shared.NombrePersona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PacienteTest {

    @Test
    @DisplayName("El DNI es obligatorio y no puede pasar de 15 caracteres")
    void dni_obligatorioYConLongitudMaxima() {
        assertThat(Dni.de(" 71234567 ").valor()).isEqualTo("71234567");
        assertThatThrownBy(() -> Dni.de(" ")).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Dni.de("1".repeat(16))).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Registrar un paciente guarda sus datos; actualizar los reemplaza")
    void registrarYActualizar() {
        Paciente paciente = Paciente.registrar(new NombrePersona("Ana", "Lopez"), Dni.de("11111111"),
                LocalDate.of(1990, 5, 1), "999", CorreoElectronico.de("ana@correo.com"), "Lima");

        paciente.actualizarDatos(new NombrePersona("Ana Maria", "Lopez"), Dni.de("22222222"), null, "888", null, "Callao");

        assertThat(paciente.nombre().completo()).isEqualTo("Ana Maria Lopez");
        assertThat(paciente.dni().valor()).isEqualTo("22222222");
        assertThat(paciente.telefono()).isEqualTo("888");
        assertThat(paciente.correo()).isNull();
        assertThat(paciente.direccion()).isEqualTo("Callao");
    }

    @Test
    @DisplayName("El telefono no puede pasar de 20 caracteres")
    void telefonoMuyLargo_lanzaError() {
        assertThatThrownBy(() -> Paciente.registrar(new NombrePersona("Ana", "Lopez"), Dni.de("1"), null,
                "9".repeat(21), null, null)).isInstanceOf(DomainException.class);
    }
}
