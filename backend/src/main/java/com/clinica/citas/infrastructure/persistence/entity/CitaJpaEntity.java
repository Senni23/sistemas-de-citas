package com.clinica.citas.infrastructure.persistence.entity;

import com.clinica.citas.domain.citas.EstadoCita;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad de persistencia (como se guarda en la tabla "citas"). Es distinta del objeto de dominio:
 * el dominio no conoce JPA, y un mapeador convierte entre los dos.
 */
@Entity
@Table(name = "citas")
public class CitaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El dominio solo conoce los ids, y por eso cada llave foranea se escribe por su columna (pacienteId,
    // medicoId, servicioId). Junto a cada una hay una asociacion @ManyToOne de solo lectura sobre la misma
    // columna: sirve para que la base cree la llave foranea y para hacer JOIN en las consultas JPQL.
    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", insertable = false, updatable = false)
    private PacienteJpaEntity paciente;

    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", insertable = false, updatable = false)
    private MedicoJpaEntity medico;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado;

    @Column(name = "servicio_id")
    private Long servicioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", insertable = false, updatable = false)
    private ServicioJpaEntity servicio;

    @Column(nullable = false)
    private boolean pagado;

    @Column(name = "monto_pagado", precision = 8, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "metodo_pago", length = 40)
    private String metodoPago;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    public CitaJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    /** Asociacion de solo lectura, pensada para las consultas JPQL; para escribir se usa el id. */
    public PacienteJpaEntity getPaciente() {
        return paciente;
    }

    /** Asociacion de solo lectura, pensada para las consultas JPQL; para escribir se usa el id. */
    public MedicoJpaEntity getMedico() {
        return medico;
    }

    /** Asociacion de solo lectura, pensada para las consultas JPQL; para escribir se usa el id. */
    public ServicioJpaEntity getServicio() {
        return servicio;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
}
