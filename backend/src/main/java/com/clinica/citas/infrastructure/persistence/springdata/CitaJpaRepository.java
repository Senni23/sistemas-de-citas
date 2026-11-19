package com.clinica.citas.infrastructure.persistence.springdata;

import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.infrastructure.persistence.entity.CitaJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repositorio de Spring Data para la tabla de citas. Mezcla los tres tipos de consulta que ofrece Spring Data JPA:
 * por nombre de metodo (findBy, countBy), JPQL con {@code @Query} y {@code @Param}, y SQL nativo.
 */
public interface CitaJpaRepository extends JpaRepository<CitaJpaEntity, Long> {

    // ---- Consultas por nombre de metodo ----

    List<CitaJpaEntity> findByPacienteId(Long pacienteId);

    long countByFecha(LocalDate fecha);

    // ---- Consultas JPQL ----

    /** Agenda de un medico; quien llama decide el orden con el parametro {@code Sort}. */
    @Query("select c from CitaJpaEntity c where c.medicoId = :medicoId")
    List<CitaJpaEntity> agendaDelMedico(@Param("medicoId") Long medicoId, Sort sort);

    /**
     * Citas de a una pagina; si {@code estado} es null no filtra. El orden y el tamano de la pagina viajan
     * dentro de {@code pageable}.
     */
    @Query("select c from CitaJpaEntity c where (:estado is null or c.estado = :estado)")
    Page<CitaJpaEntity> buscar(@Param("estado") EstadoCita estado, Pageable pageable);

    /**
     * Indica si el medico ya tiene una cita en ese dia y hora que no este en el estado ignorado (cancelada).
     * {@code excluirId} permite no contar la propia cita cuando se reprograma; puede ser null.
     */
    @Query("""
            select count(c) > 0 from CitaJpaEntity c
            where c.medicoId = :medicoId and c.fecha = :fecha and c.hora = :hora
              and c.estado <> :estadoIgnorado
              and (:excluirId is null or c.id <> :excluirId)
            """)
    boolean existeCruce(@Param("medicoId") Long medicoId, @Param("fecha") LocalDate fecha, @Param("hora") LocalTime hora,
                        @Param("estadoIgnorado") EstadoCita estadoIgnorado, @Param("excluirId") Long excluirId);

    /** Reporte: citas por especialidad. Cruza tres tablas con JOIN usando las relaciones de las entidades. */
    @Query("""
            select e.nombre as especialidad, count(c) as total
            from CitaJpaEntity c join c.medico m join m.especialidad e
            where c.estado <> :estadoIgnorado
            group by e.nombre
            order by count(c) desc, e.nombre asc
            """)
    List<ConteoPorEspecialidad> contarPorEspecialidad(@Param("estadoIgnorado") EstadoCita estadoIgnorado);

    // ---- Consulta nativa (SQL puro) ----

    /** Reporte: citas por estado. Cada fila es {estado, total}. */
    @Query(value = "SELECT estado, COUNT(*) FROM citas GROUP BY estado ORDER BY estado", nativeQuery = true)
    List<Object[]> contarPorEstado();

    /** Forma de cada fila del reporte por especialidad (una proyeccion de Spring Data). */
    interface ConteoPorEspecialidad {
        String getEspecialidad();

        Long getTotal();
    }
}
