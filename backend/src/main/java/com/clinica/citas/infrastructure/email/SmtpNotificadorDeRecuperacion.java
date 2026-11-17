package com.clinica.citas.infrastructure.email;

import com.clinica.citas.application.identidad.NotificadorDeRecuperacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Implementacion del puerto NotificadorDeRecuperacion: envia el enlace por correo (Gmail SMTP). */
@Component
public class SmtpNotificadorDeRecuperacion implements NotificadorDeRecuperacion {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public SmtpNotificadorDeRecuperacion(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarEnlace(String destinatario, String token) {
        String enlace = frontendUrl + "/restablecer-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("MEDICENTER - Recupera tu contrasena");
        mensaje.setText(
                "Hola,\n\n" +
                "Recibimos una solicitud para restablecer la contrasena de tu cuenta en MEDICENTER.\n\n" +
                "Para crear una nueva contrasena, ingresa al siguiente enlace (valido por 1 hora):\n" +
                enlace + "\n\n" +
                "Si tu no solicitaste este cambio, puedes ignorar este correo.\n\n" +
                "Atentamente,\nMEDICENTER"
        );
        mailSender.send(mensaje);
    }
}
