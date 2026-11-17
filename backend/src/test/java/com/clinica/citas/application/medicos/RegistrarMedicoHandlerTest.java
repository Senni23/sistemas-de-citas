package com.clinica.citas.application.medicos;

import com.clinica.citas.application.medicos.command.MedicoCommands;
import com.clinica.citas.application.medicos.handler.EliminarMedicoHandler;
import com.clinica.citas.application.medicos.handler.RegistrarMedicoHandler;
import com.clinica.citas.application.medicos.view.MedicoViewFactory;
import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.identidad.PasswordHash;
import com.clinica.citas.domain.identidad.PasswordHasher;
import com.clinica.citas.domain.identidad.Rol;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import com.clinica.citas.domain.medicos.Colegiatura;
import com.clinica.citas.domain.medicos.Medico;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.NombrePersona;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegistrarMedicoHandlerTest {

    private static final PasswordHash HASH = new PasswordHash("$2a$10$hash");

    @Mock
    private MedicoRepository medicos;
    @Mock
    private EspecialidadRepository especialidades;
    @Mock
    private UsuarioRepository usuarios;
    @Mock
    private PasswordHasher hasher;
    @Mock
    private MedicoViewFactory vistas;

    private RegistrarMedicoHandler handler;

    @BeforeEach
    void preparar() {
        handler = new RegistrarMedicoHandler(medicos, especialidades, usuarios, hasher, vistas);
        when(hasher.cifrar("clave123")).thenReturn(HASH);
        when(especialidades.buscarPorId(new EspecialidadId(1L))).thenReturn(Optional.of(Especialidad.reconstruir(new EspecialidadId(1L), "Pediatria", null)));
        when(medicos.guardar(any())).thenReturn(Medico.reconstruir(new MedicoId(9L), new NombrePersona("Juan", "Perez"),
                Colegiatura.reconstruir("CMP 1"), null, CorreoElectronico.reconstruir("dr@clinica.pe"), new EspecialidadId(1L)));
    }

    private MedicoCommands.Registrar comando() {
        return new MedicoCommands.Registrar("Juan", "Perez", "CMP 1", "987", "dr@clinica.pe", 1L, "clave123");
    }

    @Test
    @DisplayName("Registrar un medico crea tambien su cuenta de acceso con rol MEDICO y contrasena cifrada")
    void registrar_creaElMedicoYSuCuenta() {
        when(usuarios.buscarPorUsername("dr@clinica.pe")).thenReturn(Optional.empty());

        handler.handle(comando());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).guardar(captor.capture());
        assertThat(captor.getValue().rol()).isEqualTo(Rol.MEDICO);
        assertThat(captor.getValue().username()).isEqualTo("dr@clinica.pe");
        assertThat(captor.getValue().medicoId()).isEqualTo(new MedicoId(9L));
        assertThat(captor.getValue().passwordHash()).isEqualTo(HASH);
    }

    @Test
    @DisplayName("No se registra un medico con un correo que ya tiene cuenta")
    void registrar_correoDuplicado_lanzaRegistroDuplicado() {
        when(usuarios.buscarPorUsername("dr@clinica.pe")).thenReturn(Optional.of(Usuario.administrador("dr@clinica.pe", HASH)));

        assertThatThrownBy(() -> handler.handle(comando())).isInstanceOf(RegistroDuplicadoException.class);
        verify(medicos, never()).guardar(any());
    }

    @Test
    @DisplayName("Si la especialidad no existe se responde 'no encontrado' y no se crea nada")
    void registrar_especialidadInexistente_lanzaNoEncontrado() {
        when(usuarios.buscarPorUsername(any())).thenReturn(Optional.empty());
        when(especialidades.buscarPorId(new EspecialidadId(1L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(comando())).isInstanceOf(RecursoNoEncontradoException.class);
        verify(medicos, never()).guardar(any());
        verify(usuarios, never()).guardar(any());
    }

    @Test
    @DisplayName("Eliminar un medico borra primero su cuenta y luego al medico")
    void eliminar_borraLaCuentaYElMedico() {
        when(medicos.buscarPorId(new MedicoId(9L))).thenReturn(Optional.of(Medico.reconstruir(new MedicoId(9L),
                new NombrePersona("Juan", "Perez"), Colegiatura.reconstruir("CMP 1"), null, null, new EspecialidadId(1L))));

        new EliminarMedicoHandler(medicos, usuarios).handle(new MedicoCommands.Eliminar(9L));

        InOrder orden = inOrder(usuarios, medicos);
        orden.verify(usuarios).eliminarPorMedico(new MedicoId(9L));
        orden.verify(medicos).eliminar(new MedicoId(9L));
    }
}
