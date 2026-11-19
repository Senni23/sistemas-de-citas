package com.clinica.citas;

import com.clinica.citas.domain.medicos.Medico;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion: levantan la aplicacion completa (con H2 en memoria, perfil "test") y recorren las
 * cuatro capas: controlador -> caso de uso -> dominio -> repositorio. Tambien comprueban que las respuestas
 * JSON mantienen la forma que espera el frontend de Angular.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SeguridadYCitasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    private JsonNode login(String username, String password) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta);
    }

    private String token(String username, String password) throws Exception {
        return login(username, password).get("token").asText();
    }

    private JsonNode registrarPaciente(String dni, String correo) throws Exception {
        String cuerpo = "{\"nombres\":\"Prueba\",\"apellidos\":\"Integracion\",\"dni\":\"" + dni + "\","
                + "\"telefono\":\"900000000\",\"email\":\"" + correo + "\",\"direccion\":\"Lima\",\"password\":\"clave123\"}";
        String respuesta = mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta);
    }

    private Medico medico(int posicion) {
        return medicoRepository.listar().get(posicion);
    }

    private Paciente paciente(int posicion) {
        return pacienteRepository.listar().get(posicion);
    }

    private String citaJson(Long pacienteId, Long medicoId, String fecha, String hora) {
        return "{\"paciente\":{\"id\":" + pacienteId + "},\"medico\":{\"id\":" + medicoId + "},"
                + "\"fecha\":\"" + fecha + "\",\"hora\":\"" + hora + "\",\"motivo\":\"Control\"}";
    }

    @Test
    @DisplayName("Sin token, los datos de pacientes no son accesibles (401)")
    void pacientes_sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/pacientes")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Una clave incorrecta no entrega token (401)")
    void login_conClaveIncorrecta_devuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("El administrador entra al listado de pacientes y al inventario de farmacia")
    void admin_accedeAPacientesYFarmacia() throws Exception {
        String admin = token("admin", "123456");

        mockMvc.perform(get("/api/pacientes").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/farmacia/productos").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].stockMinimo").exists())
                .andExpect(jsonPath("$[0].precioUnitario").exists());
    }

    @Test
    @DisplayName("Un paciente no puede entrar a pacientes ni a farmacia (403)")
    void paciente_noAccedeAPanelDeAdministracion() throws Exception {
        String paciente = registrarPaciente("91000001", "rol.paciente@prueba.com").get("token").asText();

        mockMvc.perform(get("/api/pacientes").header("Authorization", "Bearer " + paciente))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/farmacia/productos").header("Authorization", "Bearer " + paciente))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un medico solo ve su propia agenda: pedir la de otro medico da 403")
    void medico_noVeLaAgendaDeOtroMedico() throws Exception {
        String medicoToken = token("juan.espinoza@clinica.pe", "medico123");
        Medico otro = medicoRepository.listar().stream()
                .filter(m -> !"juan.espinoza@clinica.pe".equals(m.correo().valor()))
                .findFirst().orElseThrow();

        mockMvc.perform(get("/api/citas/medico/" + otro.id().valor()).header("Authorization", "Bearer " + medicoToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Agendar dos citas del mismo medico en la misma fecha y hora: la primera 201, la segunda 409")
    void citas_mismoMedicoFechaYHora_segundaEsRechazadaConConflicto() throws Exception {
        String admin = token("admin", "123456");
        String cuerpo = citaJson(paciente(0).id().valor(), medico(0).id().valor(), "2026-12-15", "10:00:00");

        mockMvc.perform(post("/api/citas").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/citas").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El medico ya tiene una cita en esa fecha y hora"));
    }

    @Test
    @DisplayName("Una hora fuera del horario de atencion es rechazada con 400")
    void citas_fueraDeHorario_devuelve400() throws Exception {
        String admin = token("admin", "123456");
        String cuerpo = citaJson(paciente(0).id().valor(), medico(0).id().valor(), "2026-12-16", "15:00:00");

        String respuesta = mockMvc.perform(post("/api/citas").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertThat(respuesta).contains("horario de atencion");
    }

    @Test
    @DisplayName("La respuesta de una cita trae los datos anidados del paciente, medico y servicio, como espera el frontend")
    void citas_laRespuestaIncluyeLosDatosAnidados() throws Exception {
        String admin = token("admin", "123456");
        Long servicioId = objectMapper.readTree(mockMvc.perform(get("/api/servicios").header("Authorization", "Bearer " + admin))
                .andReturn().getResponse().getContentAsString()).get(0).get("id").asLong();
        String cuerpo = "{\"paciente\":{\"id\":" + paciente(1).id().valor() + "},\"medico\":{\"id\":" + medico(1).id().valor() + "},"
                + "\"servicio\":{\"id\":" + servicioId + "},\"fecha\":\"2026-12-17\",\"hora\":\"09:00:00\",\"motivo\":\"Chequeo\"}";

        mockMvc.perform(post("/api/citas").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.paciente.nombres").isNotEmpty())
                .andExpect(jsonPath("$.paciente.dni").isNotEmpty())
                .andExpect(jsonPath("$.medico.especialidad.nombre").isNotEmpty())
                .andExpect(jsonPath("$.servicio.precio").isNumber())
                .andExpect(jsonPath("$.servicio.especialidad.nombre").isNotEmpty())
                .andExpect(jsonPath("$.fecha").value("2026-12-17"))
                .andExpect(jsonPath("$.hora").value("09:00:00"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.pagado").value(false))
                .andExpect(jsonPath("$.montoPagado").isNumber());
    }

    @Test
    @DisplayName("Un paciente solo agenda para si mismo, aunque el formulario indique a otro paciente")
    void paciente_soloAgendaParaSiMismo() throws Exception {
        JsonNode sesion = registrarPaciente("91000002", "agenda.propia@prueba.com");
        String token = sesion.get("token").asText();
        long propioId = sesion.get("entidadId").asLong();
        long otroId = paciente(0).id().valor();
        String cuerpo = citaJson(otroId, medico(2).id().valor(), "2026-12-18", "11:00:00");

        mockMvc.perform(post("/api/citas").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paciente.id").value(propioId));
    }

    @Test
    @DisplayName("Un paciente puede cancelar su cita pero no confirmarla; el personal si puede cambiar el estado")
    void estado_pacienteSoloCancelaYPersonalCambia() throws Exception {
        JsonNode sesion = registrarPaciente("91000003", "estado.cita@prueba.com");
        String token = sesion.get("token").asText();
        String admin = token("admin", "123456");
        String creada = mockMvc.perform(post("/api/citas").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(citaJson(sesion.get("entidadId").asLong(), medico(3).id().valor(), "2026-12-19", "12:00:00")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long citaId = objectMapper.readTree(creada).get("id").asLong();

        mockMvc.perform(patch("/api/citas/" + citaId + "/estado").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"CONFIRMADA\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/citas/" + citaId + "/estado").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"CONFIRMADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
        mockMvc.perform(patch("/api/citas/" + citaId + "/estado").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"CANCELADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    @DisplayName("Registrar dos veces el mismo DNI da 409 (conflicto)")
    void registro_dniDuplicado_devuelve409() throws Exception {
        registrarPaciente("91000004", "primero@prueba.com");

        mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombres\":\"Otro\",\"apellidos\":\"Paciente\",\"dni\":\"91000004\",\"telefono\":\"1\","
                                + "\"email\":\"segundo@prueba.com\",\"direccion\":\"Lima\",\"password\":\"clave123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("El resumen del panel trae los cuatro totales")
    void dashboard_traeLosCuatroTotales() throws Exception {
        String admin = token("admin", "123456");

        mockMvc.perform(get("/api/dashboard/resumen").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPacientes").isNumber())
                .andExpect(jsonPath("$.totalMedicos").isNumber())
                .andExpect(jsonPath("$.totalCitas").isNumber())
                .andExpect(jsonPath("$.citasHoy").isNumber());
    }

    @Test
    @DisplayName("Los medicos y sus especialidades se listan con la especialidad anidada")
    void medicos_seListanConSuEspecialidad() throws Exception {
        String admin = token("admin", "123456");

        mockMvc.perform(get("/api/medicos").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].colegiatura").isNotEmpty())
                .andExpect(jsonPath("$[0].especialidad.nombre").isNotEmpty());
        mockMvc.perform(get("/api/especialidades").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));
    }

    @Test
    @DisplayName("El inicio de sesion de un medico devuelve su rol, su nombre y el id de su ficha")
    void login_medico_devuelveRolNombreEId() throws Exception {
        JsonNode sesion = login("juan.espinoza@clinica.pe", "medico123");

        assertThat(sesion.get("rol").asText()).isEqualTo("MEDICO");
        assertThat(sesion.get("nombre").asText()).isEqualTo("Juan Carlos Espinoza Ramos");
        assertThat(sesion.get("entidadId").asLong()).isPositive();
    }
}
