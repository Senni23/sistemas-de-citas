package com.clinica.citas.domain.identidad;

/** Puerto del dominio para cifrar y comparar contrasenas. La infraestructura lo implementa (BCrypt). */
public interface PasswordHasher {

    PasswordHash cifrar(String contrasenaPlana);

    boolean coincide(String contrasenaPlana, PasswordHash hash);
}
