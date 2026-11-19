package com.clinica.citas.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjetosDeValorTest {

    @Test
    @DisplayName("Dinero siempre tiene 2 decimales y no acepta montos negativos")
    void dinero_dosDecimalesYNoNegativo() {
        assertThat(new Dinero(new BigDecimal("30")).valor()).hasToString("30.00");
        assertThat(Dinero.de("8.5").valor()).hasToString("8.50");
        assertThatThrownBy(() -> new Dinero(new BigDecimal("-1"))).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> new Dinero(null)).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("NombrePersona exige nombres y apellidos, y entrega el nombre completo")
    void nombrePersona_obligatorioYCompleto() {
        assertThat(new NombrePersona("  Maria ", "Garcia ").completo()).isEqualTo("Maria Garcia");
        assertThatThrownBy(() -> new NombrePersona("", "Garcia")).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> new NombrePersona("Maria", null)).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("CorreoElectronico valida el formato; el correo opcional vacio es null")
    void correo_validaFormato() {
        assertThat(CorreoElectronico.de("ana@correo.com").valor()).isEqualTo("ana@correo.com");
        assertThatThrownBy(() -> CorreoElectronico.de("sin-arroba")).isInstanceOf(DomainException.class);
        assertThat(CorreoElectronico.opcional("  ")).isNull();
        assertThat(CorreoElectronico.opcional(null)).isNull();
    }

    @Test
    @DisplayName("Reconstruir un correo antiguo no lo vuelve a validar")
    void correo_reconstruirNoValida() {
        assertThat(CorreoElectronico.reconstruir("dato-antiguo").valor()).isEqualTo("dato-antiguo");
    }
}
