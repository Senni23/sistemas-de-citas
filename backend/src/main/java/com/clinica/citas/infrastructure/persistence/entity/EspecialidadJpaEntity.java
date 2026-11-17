package com.clinica.citas.infrastructure.persistence.entity;

import jakarta.persistence.*;

/**
 * Entidad de persistencia (como se guarda en la tabla "especialidades"). Es distinta del objeto de dominio:
 * el dominio no conoce JPA, y un mapeador convierte entre los dos.
 */
@Entity
@Table(name = "especialidades")
public class EspecialidadJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    public EspecialidadJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
