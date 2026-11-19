package com.clinica.citas.infrastructure.security;

import com.clinica.citas.domain.identidad.PasswordHash;
import com.clinica.citas.domain.identidad.Rol;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioId;
import com.clinica.citas.domain.medicos.MedicoId;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRETO = "secreto-de-pruebas-con-mas-de-32-bytes-123456";

    private Usuario usuario(String username, Rol rol) {
        return Usuario.reconstruir(new UsuarioId(4L), username, new PasswordHash("hash"), rol,
                rol == Rol.MEDICO ? new MedicoId(9L) : null, null, null);
    }

    @Test
    @DisplayName("Un token generado identifica al usuario y es valido para ese mismo usuario")
    void tokenGenerado_esValidoParaSuUsuario() {
        JwtService jwt = new JwtService(SECRETO, 60_000);
        Usuario medico = usuario("dr@clinica.pe", Rol.MEDICO);

        String token = jwt.emitir(medico);

        assertThat(jwt.extraerUsername(token)).isEqualTo("dr@clinica.pe");
        assertThat(jwt.esTokenValido(token, new UsuarioPrincipal(medico))).isTrue();
    }

    @Test
    @DisplayName("Un token emitido para un usuario no es valido para otro")
    void token_noEsValidoParaOtroUsuario() {
        JwtService jwt = new JwtService(SECRETO, 60_000);
        String token = jwt.emitir(usuario("ana@correo.com", Rol.PACIENTE));

        assertThat(jwt.esTokenValido(token, new UsuarioPrincipal(usuario("otra@correo.com", Rol.PACIENTE)))).isFalse();
    }

    @Test
    @DisplayName("Un token alterado es rechazado por la verificacion de firma")
    void tokenAlterado_esRechazado() {
        JwtService jwt = new JwtService(SECRETO, 60_000);
        String token = jwt.emitir(usuario("admin", Rol.ADMIN));
        String alterado = token.substring(0, token.length() - 3) + "abc";

        assertThatThrownBy(() -> jwt.extraerUsername(alterado)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Un token firmado con otra clave es rechazado")
    void tokenDeOtraClave_esRechazado() {
        JwtService emisor = new JwtService("otra-clave-distinta-con-mas-de-32-bytes-9999", 60_000);
        JwtService receptor = new JwtService(SECRETO, 60_000);
        String token = emisor.emitir(usuario("admin", Rol.ADMIN));

        assertThatThrownBy(() -> receptor.extraerUsername(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Un token vencido es rechazado")
    void tokenVencido_esRechazado() {
        JwtService jwt = new JwtService(SECRETO, -1_000);
        String token = jwt.emitir(usuario("admin", Rol.ADMIN));

        assertThatThrownBy(() -> jwt.extraerUsername(token)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("El principal de seguridad expone el rol como autoridad y los ids del medico o paciente")
    void principal_exponeRolEIds() {
        UsuarioPrincipal principal = new UsuarioPrincipal(usuario("dr@clinica.pe", Rol.MEDICO));

        assertThat(principal.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_MEDICO");
        assertThat(principal.getMedicoId()).isEqualTo(9L);
        assertThat(principal.getPacienteId()).isNull();
    }
}
