package com.clinica.citas.domain.citas;

import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.DomainException;

import java.time.LocalDateTime;

/**
 * Raiz de agregado de la agenda. Una cita nace PENDIENTE y sin pagar, con el monto del servicio
 * elegido; luego se puede reprogramar, cambiar de estado o pagar. Referencia al paciente, al medico
 * y al servicio solo por su identificador (cada uno es su propio agregado).
 */
public final class Cita {

    private static final int MAXIMO_MOTIVO = 255;

    private final CitaId id;
    private PacienteId pacienteId;
    private MedicoId medicoId;
    private final ServicioId servicioId;
    private HorarioCita horario;
    private String motivo;
    private EstadoCita estado;
    private Dinero monto;
    private PagoCita pago;

    private Cita(CitaId id, PacienteId pacienteId, MedicoId medicoId, ServicioId servicioId, HorarioCita horario,
                 String motivo, EstadoCita estado, Dinero monto, PagoCita pago) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.servicioId = servicioId;
        this.horario = horario;
        this.motivo = motivo;
        this.estado = estado;
        this.monto = monto;
        this.pago = pago;
    }

    /** Agenda una cita nueva: queda PENDIENTE, sin pagar, y con el precio del servicio como monto. */
    public static Cita agendar(PacienteId pacienteId, MedicoId medicoId, ServicioId servicioId, Dinero montoDelServicio,
                               HorarioCita horario, String motivo) {
        exigirParticipantes(pacienteId, medicoId, horario);
        return new Cita(null, pacienteId, medicoId, servicioId, horario, validarMotivo(motivo),
                EstadoCita.PENDIENTE, montoDelServicio, null);
    }

    /** Reconstruye una cita ya guardada (la usan los mapeadores de la infraestructura). */
    public static Cita reconstruir(CitaId id, PacienteId pacienteId, MedicoId medicoId, ServicioId servicioId,
                                   HorarioCita horario, String motivo, EstadoCita estado, Dinero monto, PagoCita pago) {
        return new Cita(id, pacienteId, medicoId, servicioId, horario, motivo, estado, monto, pago);
    }

    public void reprogramar(PacienteId nuevoPaciente, MedicoId nuevoMedico, HorarioCita nuevoHorario, String nuevoMotivo) {
        exigirParticipantes(nuevoPaciente, nuevoMedico, nuevoHorario);
        this.pacienteId = nuevoPaciente;
        this.medicoId = nuevoMedico;
        this.horario = nuevoHorario;
        this.motivo = validarMotivo(nuevoMotivo);
    }

    public void cambiarEstado(EstadoCita nuevoEstado) {
        if (nuevoEstado == null) {
            throw new DomainException("El estado de la cita es obligatorio");
        }
        this.estado = nuevoEstado;
    }

    /** Registra el pago (simulado): la cita queda pagada y CONFIRMADA. */
    public void pagar(String metodoPago, LocalDateTime ahora, Dinero precioDelServicio) {
        this.pago = new PagoCita(metodoPago, ahora);
        if (precioDelServicio != null) {
            this.monto = precioDelServicio;
        }
        this.estado = EstadoCita.CONFIRMADA;
    }

    public boolean estaPagada() {
        return pago != null;
    }

    public boolean estaActiva() {
        return estado != EstadoCita.CANCELADA;
    }

    private static void exigirParticipantes(PacienteId pacienteId, MedicoId medicoId, HorarioCita horario) {
        if (pacienteId == null || medicoId == null || horario == null) {
            throw new DomainException("La cita necesita paciente, medico, fecha y hora");
        }
    }

    private static String validarMotivo(String motivo) {
        if (motivo != null && motivo.length() > MAXIMO_MOTIVO) {
            throw new DomainException("El motivo no puede pasar de " + MAXIMO_MOTIVO + " caracteres");
        }
        return motivo;
    }

    public CitaId id() {
        return id;
    }

    public PacienteId pacienteId() {
        return pacienteId;
    }

    public MedicoId medicoId() {
        return medicoId;
    }

    public ServicioId servicioId() {
        return servicioId;
    }

    public HorarioCita horario() {
        return horario;
    }

    public String motivo() {
        return motivo;
    }

    public EstadoCita estado() {
        return estado;
    }

    public Dinero monto() {
        return monto;
    }

    public PagoCita pago() {
        return pago;
    }
}
