package com.clinica.citas.domain.catalogo;

import com.clinica.citas.domain.shared.DomainException;

/** Raiz de agregado: una especialidad medica del catalogo clinico (por ejemplo, Pediatria). */
public final class Especialidad {

    private final EspecialidadId id;
    private String nombre;
    private String descripcion;

    private Especialidad(EspecialidadId id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static Especialidad crear(String nombre, String descripcion) {
        return new Especialidad(null, validarNombre(nombre), validarDescripcion(descripcion));
    }

    public static Especialidad reconstruir(EspecialidadId id, String nombre, String descripcion) {
        return new Especialidad(id, nombre, descripcion);
    }

    public void actualizar(String nombre, String descripcion) {
        this.nombre = validarNombre(nombre);
        this.descripcion = validarDescripcion(descripcion);
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre de la especialidad es obligatorio");
        }
        if (nombre.trim().length() > 100) {
            throw new DomainException("El nombre de la especialidad no puede pasar de 100 caracteres");
        }
        return nombre.trim();
    }

    private static String validarDescripcion(String descripcion) {
        if (descripcion != null && descripcion.length() > 255) {
            throw new DomainException("La descripcion no puede pasar de 255 caracteres");
        }
        return descripcion;
    }

    public EspecialidadId id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public String descripcion() {
        return descripcion;
    }
}
