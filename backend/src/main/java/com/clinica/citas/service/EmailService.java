package com.clinica.citas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreoRecuperacion(String destinatario, String token) {
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
