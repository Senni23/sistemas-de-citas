package com.clinica.citas.application.identidad.view;

/** Lo que recibe el frontend al iniciar sesion o registrarse: el token y los datos basicos de la persona. */
public record SesionView(String token, String username, String rol, String nombre, Long entidadId) {
}
