package com.clinica.citas.infrastructure.persistence.entity;

import com.clinica.citas.domain.identidad.Rol;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de persistencia (como se guarda en la tabla "usuarios"). Es distinta del objeto de dominio:
 * el dominio no conoce JPA, y un mapeador convierte entre los dos.
 */
@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    // Cada cuenta pertenece a lo sumo a un medico o a un paciente (relacion uno a uno). Se escribe por el id;
    // la asociacion es de solo lectura (ver el comentario de CitaJpaEntity).
    @Column(name = "medico_id")
    private Long medicoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", insertable = false, updatable = false)
    private MedicoJpaEntity medico;

    @Column(name = "paciente_id")
    private Long pacienteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", insertable = false, updatable = false)
    private PacienteJpaEntity paciente;

    @Column(name = "reset_token", length = 100)
    private String resetToken;

    @Column(name = "reset_token_expira")
    private LocalDateTime resetTokenExpira;

    public UsuarioJpaEntity() {
    }

    public MedicoJpaEntity getMedico() {
        return medico;
    }

    public PacienteJpaEntity getPaciente() {
        return paciente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpira() {
        return resetTokenExpira;
    }

    public void setResetTokenExpira(LocalDateTime resetTokenExpira) {
        this.resetTokenExpira = resetTokenExpira;
    }
}
