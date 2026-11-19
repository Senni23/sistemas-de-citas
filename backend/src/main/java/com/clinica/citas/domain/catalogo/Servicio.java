package com.clinica.citas.domain.catalogo;

import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.DomainException;

/** Raiz de agregado: un servicio que ofrece la clinica, con su precio (por ejemplo, Hemograma, S/ 50). */
public final class Servicio {

    private final ServicioId id;
    private final String nombre;
    private final Dinero precio;
    private final String duracionEstimada;
    private final EspecialidadId especialidadId;

    private Servicio(ServicioId id, String nombre, Dinero precio, String duracionEstimada, EspecialidadId especialidadId) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.duracionEstimada = duracionEstimada;
        this.especialidadId = especialidadId;
    }

    public static Servicio crear(String nombre, Dinero precio, String duracionEstimada, EspecialidadId especialidadId) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del servicio es obligatorio");
        }
        if (precio == null || especialidadId == null) {
            throw new DomainException("El servicio necesita precio y especialidad");
        }
        return new Servicio(null, nombre.trim(), precio, duracionEstimada, especialidadId);
    }

    public static Servicio reconstruir(ServicioId id, String nombre, Dinero precio, String duracionEstimada,
                                       EspecialidadId especialidadId) {
        return new Servicio(id, nombre, precio, duracionEstimada, especialidadId);
    }

    public ServicioId id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public Dinero precio() {
        return precio;
    }

    public String duracionEstimada() {
        return duracionEstimada;
    }

    public EspecialidadId especialidadId() {
        return especialidadId;
    }
}
