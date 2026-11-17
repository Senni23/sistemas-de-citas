package com.clinica.citas.application.identidad;

/** Puerto: avisa al usuario con el enlace para restablecer su contrasena. La infraestructura lo implementa por correo. */
public interface NotificadorDeRecuperacion {

    void enviarEnlace(String destinatario, String token);
}
