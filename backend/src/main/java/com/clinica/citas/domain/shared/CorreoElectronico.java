package com.clinica.citas.domain.shared;

import java.util.Objects;
import java.util.regex.Pattern;

/** Objeto de valor: un correo electronico con formato valido. */
public final class CorreoElectronico {

    private static final Pattern FORMATO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String valor;

    private CorreoElectronico(String valor) {
        this.valor = valor;
    }

    /** Crea un correo nuevo validando su formato. */
    public static CorreoElectronico de(String texto) {
        if (texto == null || !FORMATO.matcher(texto.trim()).matches()) {
            throw new DomainException("El correo electronico no tiene un formato valido");
        }
        return new CorreoElectronico(texto.trim());
    }

    /** Crea un correo solo si hay texto; si viene vacio devuelve null (el correo es opcional). */
    public static CorreoElectronico opcional(String texto) {
        return texto == null || texto.isBlank() ? null : de(texto);
    }

    /** Reconstruye desde la base de datos sin validar, para no romper datos antiguos. */
    public static CorreoElectronico reconstruir(String texto) {
        return texto == null ? null : new CorreoElectronico(texto);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof CorreoElectronico c && Objects.equals(valor, c.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
