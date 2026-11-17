package com.clinica.citas.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad de persistencia (como se guarda en la tabla "servicios"). Es distinta del objeto de dominio:
 * el dominio no conoce JPA, y un mapeador convierte entre los dos.
 */
@Entity
@Table(name = "servicios")
public class ServicioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal precio;

    @Column(name = "duracion_estimada", length = 100)
    private String duracionEstimada;

    @Column(name = "especialidad_id", nullable = false)
    private Long especialidadId;

    public ServicioJpaEntity() {
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(String duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public Long getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(Long especialidadId) {
        this.especialidadId = especialidadId;
    }
}
