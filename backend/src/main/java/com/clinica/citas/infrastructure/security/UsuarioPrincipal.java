package com.clinica.citas.infrastructure.security;

import com.clinica.citas.domain.identidad.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Envuelve al Usuario del dominio para que Spring Security lo entienda, y expone el id del medico o
 * paciente asociado para que los controladores validen que cada quien solo vea y edite lo suyo.
 */
public class UsuarioPrincipal implements UserDetails {

    private final Usuario usuario;

    public UsuarioPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Long getMedicoId() {
        return usuario.medicoId() != null ? usuario.medicoId().valor() : null;
    }

    public Long getPacienteId() {
        return usuario.pacienteId() != null ? usuario.pacienteId().valor() : null;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.passwordHash().valor();
    }

    @Override
    public String getUsername() {
        return usuario.username();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
