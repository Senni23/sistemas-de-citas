package com.clinica.citas.infrastructure.persistence.repository;

import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.CitaRepository;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.citas.OrdenDeCitas;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.Pagina;
import com.clinica.citas.domain.shared.SolicitudDePagina;
import com.clinica.citas.infrastructure.persistence.entity.CitaJpaEntity;
import com.clinica.citas.infrastructure.persistence.mapper.CitaMapper;
import com.clinica.citas.infrastructure.persistence.springdata.CitaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Implementacion del contrato CitaRepository del dominio usando Spring Data JPA y MySQL. */
@Repository
public class CitaRepositoryJpa implements CitaRepository {

    private final CitaJpaRepository jpa;
    private final CitaMapper mapper;

    public CitaRepositoryJpa(CitaJpaRepository jpa, CitaMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Cita guardar(Cita cita) {
        return mapper.toDomain(jpa.save(mapper.toEntity(cita)));
    }

    @Override
    public Optional<Cita> buscarPorId(CitaId id) {
        return jpa.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public List<Cita> listar() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cita> listarPorPaciente(PacienteId pacienteId) {
        return jpa.findByPacienteId(pacienteId.valor()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cita> listarPorMedico(MedicoId medicoId) {
        return jpa.agendaDelMedico(medicoId.valor(), Sort.by("fecha", "hora", "id")).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Pagina<Cita> buscarPagina(EstadoCita estado, OrdenDeCitas orden, boolean descendente, SolicitudDePagina solicitud) {
        Page<CitaJpaEntity> pagina = jpa.buscar(estado,
                PageRequest.of(solicitud.pagina(), solicitud.tamano(), ordenar(orden, descendente)));
        return new Pagina<>(pagina.getContent().stream().map(mapper::toDomain).toList(),
                solicitud.pagina(), solicitud.tamano(), pagina.getTotalElements());
    }

    /** Traduce el orden del dominio a campos de la entidad; siempre termina en el id para que el orden sea estable. */
    private Sort ordenar(OrdenDeCitas orden, boolean descendente) {
        Sort.Direction direccion = descendente ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort principal = switch (orden) {
            case FECHA -> Sort.by(direccion, "fecha", "hora");
            case ESTADO -> Sort.by(direccion, "estado");
            case ID -> Sort.by(direccion, "id");
        };
        return orden == OrdenDeCitas.ID ? principal : principal.and(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public void eliminar(CitaId id) {
        jpa.deleteById(id.valor());
    }

    @Override
    public long contar() {
        return jpa.count();
    }

    @Override
    public long contarPorFecha(LocalDate fecha) {
        return jpa.countByFecha(fecha);
    }

    @Override
    public boolean existeCitaActiva(MedicoId medicoId, HorarioCita horario, CitaId excluir) {
        return jpa.existeCruce(medicoId.valor(), horario.fecha(), horario.hora(), EstadoCita.CANCELADA,
                excluir != null ? excluir.valor() : null);
    }
}
