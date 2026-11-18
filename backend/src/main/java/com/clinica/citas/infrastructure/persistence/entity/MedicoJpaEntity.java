package com.clinica.citas.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de persistencia (como se guarda en la tabla "medicos"). Es distinta del objeto de dominio:
 * el dominio no conoce JPA, y un mapeador convierte entre los dos.
 */
@Entity
@Table(name = "medicos")
public class MedicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 20)
    private String colegiatura;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100, unique = true)
    private String email;

    // Se escribe por el id; la asociacion de abajo es de solo lectura (ver el comentario de CitaJpaEntity).
    @Column(name = "especialidad_id", nullable = false)
    private Long especialidadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", insertable = false, updatable = false)
    private EspecialidadJpaEntity especialidad;

    /** Lado inverso de la relacion: las citas de este medico. Solo para consultas; no se usa para guardar. */
    @OneToMany(mappedBy = "medico", fetch = FetchType.LAZY)
    private List<CitaJpaEntity> citas = new ArrayList<>();

    public MedicoJpaEntity() {
    }

    public EspecialidadJpaEntity getEspecialidad() {
        return especialidad;
    }

    public List<CitaJpaEntity> getCitas() {
        return citas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getColegiatura() {
        return colegiatura;
    }

    public void setColegiatura(String colegiatura) {
        this.colegiatura = colegiatura;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(Long especialidadId) {
        this.especialidadId = especialidadId;
    }
}
