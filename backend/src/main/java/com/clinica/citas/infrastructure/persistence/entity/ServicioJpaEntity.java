package com.clinica.citas.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad de persistencia (como se guarda en la tabla "servicios"). Es distinta del objeto de dominio:
 * el dominio no conoce JPA, y un mapeador convierte entre los dos.
 */
@Entity
@Table(name = "servicios",
        // Un servicio no se repite dentro de la misma especialidad (la misma regla que valida el dominio).
        uniqueConstraints = @UniqueConstraint(name = "uk_servicio_nombre_especialidad",
                columnNames = {"nombre", "especialidad_id"}))
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

    // Se escribe por el id; la asociacion de abajo es de solo lectura (ver el comentario de CitaJpaEntity).
    @Column(name = "especialidad_id", nullable = false)
    private Long especialidadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", insertable = false, updatable = false)
    private EspecialidadJpaEntity especialidad;

    public ServicioJpaEntity() {
    }

    public EspecialidadJpaEntity getEspecialidad() {
        return especialidad;
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
