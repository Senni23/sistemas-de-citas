package com.clinica.citas.domain.shared;

/** Objeto de valor: nombres y apellidos de una persona (paciente o medico). */
public record NombrePersona(String nombres, String apellidos) {

    private static final int MAXIMO = 100;

    public NombrePersona {
        nombres = limpiar(nombres, "Los nombres son obligatorios");
        apellidos = limpiar(apellidos, "Los apellidos son obligatorios");
    }

    public String completo() {
        return nombres + " " + apellidos;
    }

    private static String limpiar(String texto, String mensajeSiVacio) {
        if (texto == null || texto.isBlank()) {
            throw new DomainException(mensajeSiVacio);
        }
        String limpio = texto.trim();
        if (limpio.length() > MAXIMO) {
            throw new DomainException("El nombre no puede pasar de " + MAXIMO + " caracteres");
        }
        return limpio;
    }
}
