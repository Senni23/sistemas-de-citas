package com.clinica.citas.application.identidad;

import com.clinica.citas.application.identidad.command.IdentidadCommands;
import com.clinica.citas.application.identidad.handler.IniciarSesionHandler;
import com.clinica.citas.application.identidad.handler.RegistrarPacienteConCuentaHandler;
import com.clinica.citas.application.identidad.handler.RestablecerPasswordHandler;
import com.clinica.citas.application.identidad.handler.SolicitarRecuperacionHandler;
import com.clinica.citas.application.identidad.view.SesionView;
import com.clinica.citas.domain.identidad.CredencialesInvalidasException;
import com.clinica.citas.domain.identidad.PasswordHash;
import com.clinica.citas.domain.identidad.PasswordHasher;
import com.clinica.citas.domain.identidad.Rol;
import com.clinica.citas.domain.identidad.TokenInvalidoException;
import com.clinica.citas.domain.identidad.TokenRecuperacion;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioId;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IdentidadHandlersTest {

    private static final PasswordHash HASH_VIEJO = new PasswordHash("$2a$10$viejo");
    private static final PasswordHash HASH_NUEVO = new PasswordHash("$2a$10$nuevo");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 20, 15, 0);
    private static final Clock RELOJ = Clock.fixed(Instant.parse("2026-10-20T15:00:00Z"), ZoneId.of("UTC"));

    @Mock
    private UsuarioRepository usuarios;
    @Mock
    private PacienteRepository pacientes;
    @Mock
    private PasswordHasher hasher;
    @Mock
    private ConstructorDeSesion sesiones;
    @Mock
    private NotificadorDeRecuperacion notificador;

    @BeforeEach
    void preparar() {
        when(usuarios.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(hasher.cifrar("clave123")).thenReturn(HASH_NUEVO);
        when(hasher.cifrar("nueva123")).thenReturn(HASH_NUEVO);
    }

    private Usuario usuarioConToken(TokenRecuperacion token) {
        return Usuario.reconstruir(new UsuarioId(1L), "ana@correo.com", HASH_VIEJO, Rol.PACIENTE, null, new PacienteId(5L), token);
    }

    // ---------------------------------------------------------------- iniciar sesion
    @Test
    @DisplayName("Con credenciales correctas el login devuelve la sesion con token y rol")
    void login_credencialesCorrectas_devuelveSesion() {
        Usuario admin = Usuario.administrador("admin", HASH_VIEJO);
        SesionView esperada = new SesionView("token", "admin", "ADMIN", "admin", null);
        when(usuarios.buscarPorUsername("admin")).thenReturn(Optional.of(admin));
        when(hasher.coincide("123456", HASH_VIEJO)).thenReturn(true);
        when(sesiones.construir(admin)).thenReturn(esperada);

        SesionView resultado = new IniciarSesionHandler(usuarios, hasher, sesiones)
                .handle(new IdentidadCommands.IniciarSesion("admin", "123456"));

        assertThat(resultado).isSameAs(esperada);
    }

    @Test
    @DisplayName("Una contrasena incorrecta y un usuario inexistente reciben el mismo error, y no se emite token")
    void login_fallido_dejaElMismoError() {
        when(usuarios.buscarPorUsername("admin")).thenReturn(Optional.of(Usuario.administrador("admin", HASH_VIEJO)));
        when(hasher.coincide("mala", HASH_VIEJO)).thenReturn(false);
        when(usuarios.buscarPorUsername("nadie")).thenReturn(Optional.empty());
        IniciarSesionHandler handler = new IniciarSesionHandler(usuarios, hasher, sesiones);

        assertThatThrownBy(() -> handler.handle(new IdentidadCommands.IniciarSesion("admin", "mala")))
                .isInstanceOf(CredencialesInvalidasException.class).hasMessage("Usuario o contrasena incorrectos");
        assertThatThrownBy(() -> handler.handle(new IdentidadCommands.IniciarSesion("nadie", "x")))
                .isInstanceOf(CredencialesInvalidasException.class).hasMessage("Usuario o contrasena incorrectos");
        verify(sesiones, never()).construir(any());
    }

    // ---------------------------------------------------------------- registro de paciente
    private IdentidadCommands.RegistrarPacienteConCuenta registro() {
        return new IdentidadCommands.RegistrarPacienteConCuenta("Ana", "Lopez", "71234567", "999", "ana@correo.com", "Lima", "clave123");
    }

    @Test
    @DisplayName("No se registra un paciente con un DNI que ya existe")
    void registrar_dniDuplicado_lanzaRegistroDuplicado() {
        when(pacientes.buscarPorDni(Dni.de("71234567"))).thenReturn(Optional.of(Paciente.reconstruir(new PacienteId(1L),
                new NombrePersona("Otro", "Paciente"), Dni.reconstruir("71234567"), null, null, null, null)));

        assertThatThrownBy(() -> new RegistrarPacienteConCuentaHandler(pacientes, usuarios, hasher, sesiones).handle(registro()))
                .isInstanceOf(RegistroDuplicadoException.class).hasMessageContaining("DNI");
        verify(usuarios, never()).guardar(any());
    }

    @Test
    @DisplayName("No se registra un paciente con un correo que ya es usuario del sistema")
    void registrar_correoYaRegistrado_lanzaRegistroDuplicado() {
        when(pacientes.buscarPorDni(any())).thenReturn(Optional.empty());
        when(usuarios.buscarPorUsername("ana@correo.com")).thenReturn(Optional.of(usuarioConToken(null)));

        assertThatThrownBy(() -> new RegistrarPacienteConCuentaHandler(pacientes, usuarios, hasher, sesiones).handle(registro()))
                .isInstanceOf(RegistroDuplicadoException.class).hasMessageContaining("correo");
        verify(pacientes, never()).guardar(any());
    }

    @Test
    @DisplayName("Registrar un paciente guarda la contrasena cifrada y crea una cuenta con rol PACIENTE")
    void registrar_ok_cifraLaContrasenaYCreaUsuarioPaciente() {
        when(pacientes.buscarPorDni(any())).thenReturn(Optional.empty());
        when(usuarios.buscarPorUsername(any())).thenReturn(Optional.empty());
        when(pacientes.guardar(any())).thenReturn(Paciente.reconstruir(new PacienteId(5L), new NombrePersona("Ana", "Lopez"),
                Dni.reconstruir("71234567"), null, "999", CorreoElectronico.de("ana@correo.com"), "Lima"));

        new RegistrarPacienteConCuentaHandler(pacientes, usuarios, hasher, sesiones).handle(registro());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).guardar(captor.capture());
        Usuario guardado = captor.getValue();
        assertThat(guardado.rol()).isEqualTo(Rol.PACIENTE);
        assertThat(guardado.username()).isEqualTo("ana@correo.com");
        assertThat(guardado.pacienteId()).isEqualTo(new PacienteId(5L));
        assertThat(guardado.passwordHash()).isEqualTo(HASH_NUEVO);
        assertThat(guardado.passwordHash().valor()).isNotEqualTo("clave123");
    }

    // ---------------------------------------------------------------- recuperar contrasena
    @Test
    @DisplayName("Solicitar recuperacion genera un token con vigencia de 1 hora y envia el correo")
    void solicitar_correoExistente_guardaTokenYEnviaCorreo() {
        Usuario ana = usuarioConToken(null);
        when(usuarios.buscarPorUsername("ana@correo.com")).thenReturn(Optional.of(ana));

        new SolicitarRecuperacionHandler(usuarios, notificador, RELOJ)
                .handle(new IdentidadCommands.SolicitarRecuperacionPassword("ana@correo.com"));

        assertThat(ana.tokenRecuperacion()).isNotNull();
        assertThat(ana.tokenRecuperacion().expira()).isEqualTo(AHORA.plusHours(1));
        verify(usuarios).guardar(ana);
        verify(notificador).enviarEnlace("ana@correo.com", ana.tokenRecuperacion().valor());
    }

    @Test
    @DisplayName("Si el correo no existe no se envia nada y no se revela que no existe")
    void solicitar_correoInexistente_noEnviaNada() {
        when(usuarios.buscarPorUsername("nadie@correo.com")).thenReturn(Optional.empty());

        new SolicitarRecuperacionHandler(usuarios, notificador, RELOJ)
                .handle(new IdentidadCommands.SolicitarRecuperacionPassword("nadie@correo.com"));

        verify(notificador, never()).enviarEnlace(any(), any());
        verify(usuarios, never()).guardar(any());
    }

    @Test
    @DisplayName("Si falla el envio del correo, el token queda guardado y no se propaga el error")
    void solicitar_siFallaElCorreo_conservaTokenYNoPropagaError() {
        Usuario ana = usuarioConToken(null);
        when(usuarios.buscarPorUsername("ana@correo.com")).thenReturn(Optional.of(ana));
        doThrow(new RuntimeException("SMTP caido")).when(notificador).enviarEnlace(any(), any());

        assertThatCode(() -> new SolicitarRecuperacionHandler(usuarios, notificador, RELOJ)
                .handle(new IdentidadCommands.SolicitarRecuperacionPassword("ana@correo.com"))).doesNotThrowAnyException();

        verify(usuarios).guardar(ana);
        assertThat(ana.tokenRecuperacion()).isNotNull();
    }

    @Test
    @DisplayName("Con un token vigente se cambia la contrasena (cifrada) y el token se invalida")
    void restablecer_tokenValido_cambiaPasswordYLimpiaToken() {
        Usuario ana = usuarioConToken(new TokenRecuperacion("tok-1", AHORA.plusMinutes(30)));
        when(usuarios.buscarPorTokenRecuperacion("tok-1")).thenReturn(Optional.of(ana));

        new RestablecerPasswordHandler(usuarios, hasher, RELOJ).handle(new IdentidadCommands.RestablecerPassword("tok-1", "nueva123"));

        assertThat(ana.passwordHash()).isEqualTo(HASH_NUEVO);
        assertThat(ana.tokenRecuperacion()).isNull();
        verify(usuarios).guardar(ana);
    }

    @Test
    @DisplayName("Un token que no existe es rechazado")
    void restablecer_tokenInexistente_lanzaTokenInvalido() {
        when(usuarios.buscarPorTokenRecuperacion("falso")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new RestablecerPasswordHandler(usuarios, hasher, RELOJ)
                .handle(new IdentidadCommands.RestablecerPassword("falso", "nueva123")))
                .isInstanceOf(TokenInvalidoException.class);
        verify(usuarios, never()).guardar(any());
    }

    @Test
    @DisplayName("Un token vencido es rechazado y la contrasena no cambia")
    void restablecer_tokenVencido_lanzaTokenInvalido() {
        Usuario ana = usuarioConToken(new TokenRecuperacion("tok-1", AHORA.minusMinutes(1)));
        when(usuarios.buscarPorTokenRecuperacion("tok-1")).thenReturn(Optional.of(ana));

        assertThatThrownBy(() -> new RestablecerPasswordHandler(usuarios, hasher, RELOJ)
                .handle(new IdentidadCommands.RestablecerPassword("tok-1", "nueva123")))
                .isInstanceOf(TokenInvalidoException.class).hasMessageContaining("expirado");
        assertThat(ana.passwordHash()).isEqualTo(HASH_VIEJO);
        verify(usuarios, never()).guardar(eq(ana));
    }
}
