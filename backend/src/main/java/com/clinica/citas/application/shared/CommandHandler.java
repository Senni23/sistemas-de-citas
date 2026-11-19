package com.clinica.citas.application.shared;

/** Ejecuta un comando: coordina al dominio y a los repositorios para cumplir un caso de uso. */
public interface CommandHandler<C extends Command<R>, R> {

    R handle(C command);
}
