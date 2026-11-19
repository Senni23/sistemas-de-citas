package com.clinica.citas;

import com.clinica.citas.domain.medicos.MedicoRepository;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion de los listados paginados, la cabecera Location de los POST, el resumen del tablero y
 * la proteccion de la base con llaves foraneas. Usan fechas de 2027 para no chocar con las otras pruebas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ListadosYRecursosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MedicoRepository medicoRepository;

    private String token(String username, String password) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("token").asText();
    }

    private String admin() throws Exception {
        return "Bearer " + token("admin", "123456");
    }

    private Long crearPaciente(String bearer, String dni) throws Exception {
        String cuerpo = "{\"nombres\":\"Lista\",\"apellidos\":\"Paginada\",\"dni\":\"" + dni + "\"}";
        String respuesta = mockMvc.perform(post("/api/pacientes").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private Long medicoId() {
        return medicoRepository.listar().get(1).id().valor();
    }

    private MvcResult agendar(String bearer, Long pacienteId, String fecha, String hora) throws Exception {
        String cuerpo = "{\"paciente\":{\"id\":" + pacienteId + "},\"medico\":{\"id\":" + medicoId() + "},"
                + "\"fecha\":\"" + fecha + "\",\"hora\":\"" + hora + "\",\"motivo\":\"Control\"}";
        return mockMvc.perform(post("/api/citas").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private Long idDe(MvcResult resultado) throws Exception {
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @DisplayName("Al crear una cita, la respuesta 201 trae la cabecera Location con la direccion de la nueva cita")
    void crearCita_devuelveLocation() throws Exception {
        String admin = admin();
        Long paciente = crearPaciente(admin, "93000001");

        MvcResult creada = agendar(admin, paciente, "2027-03-01", "09:00:00");

        mockMvc.perform(get("/api/citas/" + idDe(creada)).header("Authorization", admin)).andExpect(status().isOk());
        assertThat(creada.getResponse().getHeader("Location"))
                .endsWith("/api/citas/" + idDe(creada));
    }

    @Test
    @DisplayName("Al crear un paciente, la respuesta 201 trae la cabecera Location")
    void crearPaciente_devuelveLocation() throws Exception {
        String cuerpo = "{\"nombres\":\"Con\",\"apellidos\":\"Ubicacion\",\"dni\":\"93000002\"}";

        mockMvc.perform(post("/api/pacientes").header("Authorization", admin())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/pacientes/")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("El navegador puede leer la cabecera Location: el servidor la declara como expuesta en CORS")
    void location_estaExpuestaPorCors() throws Exception {
        String cuerpo = "{\"nombres\":\"Cors\",\"apellidos\":\"Expuesta\",\"dni\":\"93000003\"}";

        mockMvc.perform(post("/api/pacientes").header("Authorization", admin())
                        .header("Origin", "http://localhost:4200")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(header().string("Access-Control-Expose-Headers", containsString("Location")));
    }

    @Test
    @DisplayName("GET /api/citas/buscar devuelve una pagina con sus totales y respeta el tamano pedido")
    void buscar_devuelveUnaPagina() throws Exception {
        String admin = admin();
        Long paciente = crearPaciente(admin, "93000004");
        agendar(admin, paciente, "2027-04-01", "09:00:00");
        agendar(admin, paciente, "2027-04-02", "09:00:00");
        agendar(admin, paciente, "2027-04-03", "09:00:00");

        mockMvc.perform(get("/api/citas/buscar?orden=FECHA&descendente=true&pagina=0&tamano=2")
                        .header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido", hasSize(2)))
                .andExpect(jsonPath("$.pagina", is(0)))
                .andExpect(jsonPath("$.tamano", is(2)))
                .andExpect(jsonPath("$.totalElementos", greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.totalPaginas", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.contenido[0].paciente.id").exists());
    }

    @Test
    @DisplayName("GET /api/citas/buscar puede filtrar por estado")
    void buscar_filtraPorEstado() throws Exception {
        String admin = admin();
        Long paciente = crearPaciente(admin, "93000005");
        Long cita = idDe(agendar(admin, paciente, "2027-05-01", "09:00:00"));
        mockMvc.perform(patch("/api/citas/" + cita + "/estado").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"CANCELADA\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/citas/buscar?estado=CANCELADA&tamano=50").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.contenido[*].estado", everyItem(is("CANCELADA"))));
    }

    @Test
    @DisplayName("Un paciente no puede usar el listado paginado de todas las citas (403)")
    void buscar_esSoloParaElPersonalDeLaClinica() throws Exception {
        String registro = "{\"nombres\":\"Solo\",\"apellidos\":\"Paciente\",\"dni\":\"93000006\",\"email\":\"solo.paciente@prueba.com\","
                + "\"password\":\"clave123\"}";
        String respuesta = mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON).content(registro))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String bearer = "Bearer " + objectMapper.readTree(respuesta).get("token").asText();

        mockMvc.perform(get("/api/citas/buscar").header("Authorization", bearer)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un orden inexistente o un tamano de pagina fuera de rango se rechazan con 400 y un mensaje claro")
    void buscar_conParametrosInvalidos_devuelve400() throws Exception {
        String admin = admin();

        mockMvc.perform(get("/api/citas/buscar?orden=XYZ").header("Authorization", admin))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", containsString("orden")));
        mockMvc.perform(get("/api/citas/buscar?tamano=0").header("Authorization", admin))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/citas/buscar?pagina=-1").header("Authorization", admin))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("El resumen del tablero incluye los reportes por estado y por especialidad")
    void dashboard_incluyeLosReportes() throws Exception {
        String admin = admin();
        Long paciente = crearPaciente(admin, "93000007");
        agendar(admin, paciente, "2027-06-01", "09:00:00");

        mockMvc.perform(get("/api/dashboard/resumen").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCitas", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.citasPorEstado[0].estado").exists())
                .andExpect(jsonPath("$.citasPorEstado[0].total").exists())
                .andExpect(jsonPath("$.citasPorEspecialidad[0].especialidad").exists())
                .andExpect(jsonPath("$.citasPorEspecialidad[0].total").exists());
    }

    @Test
    @DisplayName("No se puede eliminar un paciente que todavia tiene citas (409); al eliminar la cita, ya se puede")
    void eliminarPacienteConCitas_devuelve409() throws Exception {
        String admin = admin();
        Long paciente = crearPaciente(admin, "93000008");
        Long cita = idDe(agendar(admin, paciente, "2027-07-01", "09:00:00"));

        mockMvc.perform(delete("/api/pacientes/" + paciente).header("Authorization", admin))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje", containsString("citas")));

        mockMvc.perform(delete("/api/citas/" + cita).header("Authorization", admin)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/pacientes/" + paciente).header("Authorization", admin)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Un id que no es numero se rechaza con 400 en JSON, no con una pagina de error")
    void idNoNumerico_devuelve400() throws Exception {
        mockMvc.perform(get("/api/citas/abc").header("Authorization", admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", containsString("id")));
    }

    @Test
    @DisplayName("La agenda del medico llega ordenada por fecha y hora")
    void agendaDelMedico_llegaOrdenada() throws Exception {
        String admin = admin();
        Long paciente = crearPaciente(admin, "93000009");
        agendar(admin, paciente, "2027-08-10", "09:00:00");
        agendar(admin, paciente, "2027-08-02", "17:00:00");
        agendar(admin, paciente, "2027-08-02", "09:00:00");

        String respuesta = mockMvc.perform(get("/api/citas/medico/" + medicoId()).header("Authorization", admin))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode citas = objectMapper.readTree(respuesta);

        String anterior = "";
        for (JsonNode c : citas) {
            String actual = c.get("fecha").asText() + "T" + c.get("hora").asText();
            assertThat(actual).isGreaterThanOrEqualTo(anterior);
            anterior = actual;
        }
        assertThat(citas.size()).isGreaterThanOrEqualTo(3);
    }
}
