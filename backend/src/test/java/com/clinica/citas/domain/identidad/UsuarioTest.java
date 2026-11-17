package com.clinica.citas.domain.identidad;

import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 20, 10, 0);
    private static final PasswordHash VIEJA = new PasswordHash("$2a$10$hash-viejo");
    private static final PasswordHash NUEVA = new PasswordHash("$2a$10$hash-nuevo");

    private Usuario paciente() {
        return Usuario.paraPaciente("ana@correo.com", VIEJA, new PacienteId(5L));
    }

    @Test
    @DisplayName("Cada tipo de cuenta se crea con su rol y su persona asociada")
    void fabricas_asignanRolYPersona() {
        assertThat(Usuario.administrador("admin", VIEJA).rol()).isEqualTo(Rol.ADMIN);
        assertThat(paciente().rol()).isEqualTo(Rol.PACIENTE);
        assertThat(paciente().pacienteId()).isEqualTo(new PacienteId(5L));
        Usuario medico = Usuario.paraMedico("dr@clinica.pe", VIEJA, new MedicoId(9L));
        assertThat(medico.rol()).isEqualTo(Rol.MEDICO);
        assertThat(medico.medicoId()).isEqualTo(new MedicoId(9L));
    }

    @Test
    @DisplayName("Solo el personal de la clinica (admin y recepcionista) gestiona todo el sistema")
    void rol_personalClinica() {
        assertThat(Rol.ADMIN.esPersonalClinica()).isTrue();
        assertThat(Rol.RECEPCIONISTA.esPersonalClinica()).isTrue();
        assertThat(Rol.MEDICO.esPersonalClinica()).isFalse();
        assertThat(Rol.PACIENTE.esPersonalClinica()).isFalse();
    }

    @Test
    @DisplayName("Solicitar recuperacion genera un token que vence en 1 hora")
    void solicitarRecuperacion_vigenciaDeUnaHora() {
        Usuario usuario = paciente();

        TokenRecuperacion token = usuario.solicitarRecuperacion("abc-123", AHORA);

        assertThat(token.valor()).isEqualTo("abc-123");
        assertThat(token.expira()).isEqualTo(AHORA.plusHours(1));
        assertThat(usuario.tokenRecuperacion()).isEqualTo(token);
    }

    @Test
    @DisplayName("Con un token vigente se cambia la contrasena y el token se invalida (un solo uso)")
    void restablecer_conTokenVigente_cambiaYLimpiaElToken() {
        Usuario usuario = paciente();
        usuario.solicitarRecuperacion("abc-123", AHORA);

        usuario.restablecerPassword(NUEVA, AHORA.plusMinutes(59));

        assertThat(usuario.passwordHash()).isEqualTo(NUEVA);
        assertThat(usuario.tokenRecuperacion()).isNull();
    }

    @Test
    @DisplayName("Un token vencido es rechazado y la contrasena no cambia")
    void restablecer_conTokenVencido_lanzaError() {
        Usuario usuario = paciente();
        usuario.solicitarRecuperacion("abc-123", AHORA);

        assertThatThrownBy(() -> usuario.restablecerPassword(NUEVA, AHORA.plusHours(1).plusSeconds(1)))
                .isInstanceOf(TokenInvalidoException.class)
                .hasMessageContaining("expirado");
        assertThat(usuario.passwordHash()).isEqualTo(VIEJA);
    }

    @Test
    @DisplayName("Sin haber pedido recuperacion no se puede restablecer la contrasena")
    void restablecer_sinToken_lanzaError() {
        assertThatThrownBy(() -> paciente().restablecerPassword(NUEVA, AHORA)).isInstanceOf(TokenInvalidoException.class);
    }

    @Test
    @DisplayName("PasswordHash detecta hashes BCrypt, no acepta vacios y no muestra su valor")
    void passwordHash_esBcryptYSeOculta() {
        assertThat(VIEJA.esBcrypt()).isTrue();
        assertThat(new PasswordHash("123456").esBcrypt()).isFalse();
        assertThat(VIEJA.toString()).doesNotContain("hash-viejo");
        assertThatThrownBy(() -> new PasswordHash(" ")).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("La cuenta de paciente o de medico necesita su persona asociada")
    void fabricas_exigenPersona() {
        assertThatThrownBy(() -> Usuario.paraPaciente("a@b.com", VIEJA, null)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> Usuario.paraMedico("a@b.com", VIEJA, null)).isInstanceOf(DomainException.class);
    }
}
