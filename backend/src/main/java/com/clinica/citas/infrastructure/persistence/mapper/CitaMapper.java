package com.clinica.citas.infrastructure.persistence.mapper;

import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.citas.PagoCita;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.infrastructure.persistence.entity.CitaJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CitaMapper {

    private static final LocalDateTime SIN_FECHA_DE_PAGO = LocalDateTime.of(1970, 1, 1, 0, 0);

    public Cita toDomain(CitaJpaEntity e) {
        PagoCita pago = null;
        if (e.isPagado()) {
            // Datos antiguos podian tener "pagado" sin metodo o fecha: se completan para no perder el pago.
            pago = new PagoCita(e.getMetodoPago() != null && !e.getMetodoPago().isBlank() ? e.getMetodoPago() : "SIN_REGISTRO",
                    e.getFechaPago() != null ? e.getFechaPago() : SIN_FECHA_DE_PAGO);
        }
        return Cita.reconstruir(
                new CitaId(e.getId()),
                new PacienteId(e.getPacienteId()),
                new MedicoId(e.getMedicoId()),
                e.getServicioId() != null ? new ServicioId(e.getServicioId()) : null,
                HorarioCita.reconstruir(e.getFecha(), e.getHora()),
                e.getMotivo(),
                e.getEstado(),
                Dinero.reconstruir(e.getMontoPagado()),
                pago);
    }

    public CitaJpaEntity toEntity(Cita c) {
        CitaJpaEntity e = new CitaJpaEntity();
        e.setId(c.id() != null ? c.id().valor() : null);
        e.setPacienteId(c.pacienteId().valor());
        e.setMedicoId(c.medicoId().valor());
        e.setServicioId(c.servicioId() != null ? c.servicioId().valor() : null);
        e.setFecha(c.horario().fecha());
        e.setHora(c.horario().hora());
        e.setMotivo(c.motivo());
        e.setEstado(c.estado());
        e.setPagado(c.estaPagada());
        e.setMontoPagado(c.monto() != null ? c.monto().valor() : null);
        e.setMetodoPago(c.pago() != null ? c.pago().metodo() : null);
        e.setFechaPago(c.pago() != null ? c.pago().fecha() : null);
        return e;
    }
}
