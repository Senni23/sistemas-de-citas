package com.clinica.citas.infrastructure.persistence;

import com.clinica.citas.application.dashboard.port.ReportesDeCitas.ConteoPorEspecialidad;
import com.clinica.citas.application.dashboard.port.ReportesDeCitas.ConteoPorEstado;
import com.clinica.citas.domain.catalogo.ServicioId;
import com.clinica.citas.domain.citas.Cita;
import com.clinica.citas.domain.citas.CitaId;
import com.clinica.citas.domain.citas.EstadoCita;
import com.clinica.citas.domain.citas.HorarioCita;
import com.clinica.citas.domain.citas.OrdenDeCitas;
import com.clinica.citas.domain.medicos.MedicoId;
import com.clinica.citas.domain.pacientes.PacienteId;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.Pagina;
import com.clinica.citas.domain.shared.SolicitudDePagina;
import com.clinica.citas.infrastructure.persistence.entity.CitaJpaEntity;
import com.clinica.citas.infrastructure.persistence.entity.EspecialidadJpaEntity;
import com.clinica.citas.infrastructure.persistence.entity.MedicoJpaEntity;
import com.clinica.citas.infrastructure.persistence.entity.PacienteJpaEntity;
import com.clinica.citas.infrastructure.persistence.entity.ServicioJpaEntity;
import com.clinica.citas.infrastructure.persistence.mapper.CitaMapper;
import com.clinica.citas.infrastructure.persistence.repository.CitaRepositoryJpa;
import com.clinica.citas.infrastructure.persistence.repository.ReportesDeCitasJpa;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba la implementacion real del repositorio (Spring Data + mapeador) contra una base H2 en memoria.
 * Como las entidades ya tienen relaciones y la base crea las llaves foraneas, cada prueba parte de datos reales:
 * dos especialidades, dos medicos, dos pacientes y un servicio.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({CitaRepositoryJpa.class, ReportesDeCitasJpa.class, CitaMapper.class})
class CitaRepositoryJpaTest {

    private static final HorarioCita HORARIO = HorarioCita.programar(LocalDate.of(2027, 1, 10), LocalTime.of(10, 0));

    @Autowired
    private CitaRepositoryJpa repositorio;

    @Autowired
    private ReportesDeCitasJpa reportes;

    @Autowired
    private TestEntityManager em;

    private EspecialidadJpaEntity cardiologia;
    private EspecialidadJpaEntity pediatria;
    private MedicoId medicoCardiologo;
    private MedicoId medicoPediatra;
    private PacienteId paciente;
    private ServicioId servicio;

    @BeforeEach
    void crearDatosBase() {
        cardiologia = especialidad("Cardiologia");
        pediatria = especialidad("Pediatria");
        medicoCardiologo = new MedicoId(medico("CMP-001", "cardiologo@clinica.pe", cardiologia).getId());
        medicoPediatra = new MedicoId(medico("CMP-002", "pediatra@clinica.pe", pediatria).getId());

        PacienteJpaEntity p = new PacienteJpaEntity();
        p.setNombres("Ana");
        p.setApellidos("Quispe");
        p.setDni("12345678");
        paciente = new PacienteId(em.persistAndFlush(p).getId());

        servicio = new ServicioId(servicio("Consulta general", cardiologia).getId());
    }

    private EspecialidadJpaEntity especialidad(String nombre) {
        EspecialidadJpaEntity e = new EspecialidadJpaEntity();
        e.setNombre(nombre);
        return em.persistAndFlush(e);
    }

    private MedicoJpaEntity medico(String colegiatura, String email, EspecialidadJpaEntity especialidad) {
        MedicoJpaEntity m = new MedicoJpaEntity();
        m.setNombres("Medico " + colegiatura);
        m.setApellidos("Prueba");
        m.setColegiatura(colegiatura);
        m.setEmail(email);
        m.setEspecialidadId(especialidad.getId());
        return em.persistAndFlush(m);
    }

    private ServicioJpaEntity servicio(String nombre, EspecialidadJpaEntity especialidad) {
        ServicioJpaEntity s = new ServicioJpaEntity();
        s.setNombre(nombre);
        s.setPrecio(new BigDecimal("50.00"));
        s.setEspecialidadId(especialidad.getId());
        return em.persistAndFlush(s);
    }

    private Cita guardarCita(MedicoId medico, HorarioCita horario) {
        return repositorio.guardar(Cita.agendar(paciente, medico, servicio, Dinero.de("50"), horario, "Control"));
    }

    private HorarioCita dia(int diaDelMes, int hora) {
        return HorarioCita.programar(LocalDate.of(2027, 2, diaDelMes), LocalTime.of(hora, 0));
    }

    // ---------------- Guardar y leer ----------------

    @Test
    @DisplayName("Guardar y volver a buscar una cita conserva todos sus datos")
    void guardarYBuscar_conservaLosDatos() {
        Cita guardada = guardarCita(medicoCardiologo, HORARIO);

        Cita leida = repositorio.buscarPorId(guardada.id()).orElseThrow();

        assertThat(leida.id()).isNotNull();
        assertThat(leida.pacienteId()).isEqualTo(paciente);
        assertThat(leida.medicoId()).isEqualTo(medicoCardiologo);
        assertThat(leida.servicioId()).isEqualTo(servicio);
        assertThat(leida.horario()).isEqualTo(HORARIO);
        assertThat(leida.estado()).isEqualTo(EstadoCita.PENDIENTE);
        assertThat(leida.monto().valor()).isEqualByComparingTo("50.00");
        assertThat(leida.estaPagada()).isFalse();
    }

    @Test
    @DisplayName("El pago se guarda y se recupera (metodo, fecha y estado CONFIRMADA)")
    void elPagoSePersiste() {
        Cita cita = guardarCita(medicoCardiologo, HORARIO);
        cita.pagar("TARJETA_SIMULADA", LocalDateTime.of(2026, 10, 20, 12, 0), Dinero.de("50"));
        repositorio.guardar(cita);

        Cita leida = repositorio.buscarPorId(cita.id()).orElseThrow();

        assertThat(leida.estaPagada()).isTrue();
        assertThat(leida.pago().metodo()).isEqualTo("TARJETA_SIMULADA");
        assertThat(leida.pago().fecha()).isEqualTo(LocalDateTime.of(2026, 10, 20, 12, 0));
        assertThat(leida.estado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    // ---------------- Cruce de horarios (consulta JPQL con @Param) ----------------

    @Test
    @DisplayName("Una cita activa del mismo medico en la misma fecha y hora cuenta como cruce")
    void citaActiva_bloqueaElHorario() {
        guardarCita(medicoCardiologo, HORARIO);

        assertThat(repositorio.existeCitaActiva(medicoCardiologo, HORARIO, null)).isTrue();
        assertThat(repositorio.existeCitaActiva(medicoPediatra, HORARIO, null)).isFalse();
    }

    @Test
    @DisplayName("Las citas canceladas no bloquean el horario")
    void citaCancelada_noBloquea() {
        Cita cita = guardarCita(medicoCardiologo, HORARIO);
        cita.cambiarEstado(EstadoCita.CANCELADA);
        repositorio.guardar(cita);

        assertThat(repositorio.existeCitaActiva(medicoCardiologo, HORARIO, null)).isFalse();
    }

    @Test
    @DisplayName("Al reprogramar, la propia cita no cuenta como cruce consigo misma")
    void laPropiaCita_noCuentaComoCruce() {
        Cita cita = guardarCita(medicoCardiologo, HORARIO);

        assertThat(repositorio.existeCitaActiva(medicoCardiologo, HORARIO, cita.id())).isFalse();
        assertThat(repositorio.existeCitaActiva(medicoCardiologo, HORARIO, new CitaId(cita.id().valor() + 1000))).isTrue();
    }

    // ---------------- Listados, orden y paginacion ----------------

    @Test
    @DisplayName("Se pueden contar las citas del dia y listar las de un paciente o de un medico")
    void contarYListar() {
        guardarCita(medicoCardiologo, HORARIO);
        guardarCita(medicoPediatra, HorarioCita.programar(LocalDate.of(2027, 1, 10), LocalTime.of(11, 0)));

        assertThat(repositorio.contarPorFecha(LocalDate.of(2027, 1, 10))).isEqualTo(2);
        assertThat(repositorio.listarPorMedico(medicoCardiologo)).hasSize(1);
        assertThat(repositorio.listarPorPaciente(paciente)).hasSize(2);
    }

    @Test
    @DisplayName("La agenda del medico sale ordenada por fecha y hora, sin importar el orden en que se guardo")
    void laAgendaSaleOrdenada() {
        guardarCita(medicoCardiologo, dia(20, 9));
        guardarCita(medicoCardiologo, dia(5, 17));
        guardarCita(medicoCardiologo, dia(5, 9));

        assertThat(repositorio.listarPorMedico(medicoCardiologo))
                .extracting(c -> c.horario().fecha().getDayOfMonth() + "@" + c.horario().hora().getHour())
                .containsExactly("5@9", "5@17", "20@9");
    }

    @Test
    @DisplayName("La busqueda por paginas devuelve el trozo pedido, el total y respeta el orden")
    void buscarPagina_paginaYOrdena() {
        for (int d = 1; d <= 5; d++) {
            guardarCita(medicoCardiologo, dia(d, 9));
        }

        Pagina<Cita> primera = repositorio.buscarPagina(null, OrdenDeCitas.FECHA, true, new SolicitudDePagina(0, 2));
        Pagina<Cita> ultima = repositorio.buscarPagina(null, OrdenDeCitas.FECHA, true, new SolicitudDePagina(2, 2));

        assertThat(primera.totalElementos()).isEqualTo(5);
        assertThat(primera.totalPaginas()).isEqualTo(3);
        assertThat(primera.contenido()).extracting(c -> c.horario().fecha().getDayOfMonth()).containsExactly(5, 4);
        assertThat(ultima.contenido()).extracting(c -> c.horario().fecha().getDayOfMonth()).containsExactly(1);
    }

    @Test
    @DisplayName("La busqueda por paginas puede filtrar por estado")
    void buscarPagina_filtraPorEstado() {
        Cita a = guardarCita(medicoCardiologo, dia(1, 9));
        guardarCita(medicoCardiologo, dia(2, 9));
        Cita c = guardarCita(medicoCardiologo, dia(3, 9));
        a.cambiarEstado(EstadoCita.CANCELADA);
        c.cambiarEstado(EstadoCita.CANCELADA);
        repositorio.guardar(a);
        repositorio.guardar(c);

        Pagina<Cita> canceladas = repositorio.buscarPagina(EstadoCita.CANCELADA, OrdenDeCitas.ID, false, new SolicitudDePagina(0, 10));
        Pagina<Cita> pendientes = repositorio.buscarPagina(EstadoCita.PENDIENTE, OrdenDeCitas.ID, false, new SolicitudDePagina(0, 10));

        assertThat(canceladas.totalElementos()).isEqualTo(2);
        assertThat(canceladas.contenido()).allMatch(x -> x.estado() == EstadoCita.CANCELADA);
        assertThat(pendientes.totalElementos()).isEqualTo(1);
    }

    @Test
    @DisplayName("Ordenar por ID descendente deja primero la cita mas reciente")
    void buscarPagina_ordenPorIdDescendente() {
        Cita primera = guardarCita(medicoCardiologo, dia(1, 9));
        Cita segunda = guardarCita(medicoCardiologo, dia(2, 9));

        Pagina<Cita> pagina = repositorio.buscarPagina(null, OrdenDeCitas.ID, true, new SolicitudDePagina(0, 10));

        assertThat(pagina.contenido()).extracting(Cita::id).containsExactly(segunda.id(), primera.id());
    }

    // ---------------- Reportes (consulta JPQL con JOIN y consulta nativa) ----------------

    @Test
    @DisplayName("Reporte JPQL: cuenta las citas activas por especialidad cruzando cita, medico y especialidad")
    void reportePorEspecialidad() {
        guardarCita(medicoCardiologo, dia(1, 9));
        guardarCita(medicoCardiologo, dia(2, 9));
        Cita cancelada = guardarCita(medicoCardiologo, dia(3, 9));
        guardarCita(medicoPediatra, dia(1, 9));
        cancelada.cambiarEstado(EstadoCita.CANCELADA);
        repositorio.guardar(cancelada);

        assertThat(reportes.citasActivasPorEspecialidad()).containsExactly(
                new ConteoPorEspecialidad("Cardiologia", 2),
                new ConteoPorEspecialidad("Pediatria", 1));
    }

    @Test
    @DisplayName("Reporte nativo: cuenta las citas por estado con SQL")
    void reportePorEstado() {
        guardarCita(medicoCardiologo, dia(1, 9));
        guardarCita(medicoCardiologo, dia(2, 9));
        Cita cancelada = guardarCita(medicoCardiologo, dia(3, 9));
        cancelada.cambiarEstado(EstadoCita.CANCELADA);
        repositorio.guardar(cancelada);

        assertThat(reportes.citasPorEstado()).containsExactly(
                new ConteoPorEstado(EstadoCita.CANCELADA, 1),
                new ConteoPorEstado(EstadoCita.PENDIENTE, 2));
    }

    // ---------------- Relaciones JPA y llaves foraneas ----------------

    @Test
    @DisplayName("La base rechaza una cita de un medico que no existe (llave foranea)")
    void laLlaveForaneaImpideMedicosInexistentes() {
        assertThatThrownBy(() -> guardarCita(new MedicoId(9999L), HORARIO))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Las relaciones @ManyToOne permiten navegar de la cita al medico y a su especialidad")
    void sePuedeNavegarPorLasRelaciones() {
        Cita guardada = guardarCita(medicoPediatra, HORARIO);
        em.clear();

        CitaJpaEntity entidad = em.find(CitaJpaEntity.class, guardada.id().valor());

        assertThat(entidad.getPaciente().getDni()).isEqualTo("12345678");
        assertThat(entidad.getMedico().getEspecialidad().getNombre()).isEqualTo("Pediatria");
        assertThat(entidad.getServicio().getNombre()).isEqualTo("Consulta general");
    }

    @Test
    @DisplayName("Las relaciones @OneToMany (lado inverso) listan las citas del medico y los medicos de la especialidad")
    void ladoInversoDeLasRelaciones() {
        guardarCita(medicoCardiologo, dia(1, 9));
        guardarCita(medicoCardiologo, dia(2, 9));
        em.clear();

        MedicoJpaEntity medico = em.find(MedicoJpaEntity.class, medicoCardiologo.valor());
        EspecialidadJpaEntity especialidad = em.find(EspecialidadJpaEntity.class, cardiologia.getId());

        assertThat(medico.getCitas()).hasSize(2);
        assertThat(especialidad.getMedicos()).extracting(MedicoJpaEntity::getColegiatura).containsExactly("CMP-001");
        assertThat(especialidad.getServicios()).extracting(ServicioJpaEntity::getNombre).containsExactly("Consulta general");
    }

    @Test
    @DisplayName("Un servicio no se puede repetir dentro de la misma especialidad (@UniqueConstraint)")
    void servicioUnicoPorEspecialidad() {
        assertThatThrownBy(() -> servicio("Consulta general", cardiologia)).isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("El mismo nombre de servicio si se permite en otra especialidad")
    void mismoServicioEnOtraEspecialidad() {
        ServicioJpaEntity otro = servicio("Consulta general", pediatria);

        assertThat(otro.getId()).isNotNull();
    }
}
