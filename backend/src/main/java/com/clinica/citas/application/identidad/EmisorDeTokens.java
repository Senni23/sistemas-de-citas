package com.clinica.citas.application.identidad;

import com.clinica.citas.domain.identidad.Usuario;

/** Puerto: emite el token de sesion de un usuario. La infraestructura lo implementa con JWT. */
public interface EmisorDeTokens {

    String emitir(Usuario usuario);
}
