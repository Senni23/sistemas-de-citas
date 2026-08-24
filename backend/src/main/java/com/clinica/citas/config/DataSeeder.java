package com.clinica.citas.config;

import com.clinica.citas.model.*;
import com.clinica.citas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Carga datos de ejemplo al arrancar (reemplaza al antiguo data.sql). Se hizo el cambio porque
 * las contrasenas ahora se guardan hasheadas con BCrypt, algo que un script SQL plano no puede
 * calcular. Cada metodo es idempotente: si el dato ya existe, no lo vuelve a insertar.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EspecialidadRepository especialidadRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final ServicioRepository servicioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, EspecialidadRepository especialidadRepository,
                       MedicoRepository medicoRepository, PacienteRepository pacienteRepository,
                       ServicioRepository servicioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.especialidadRepository = especialidadRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.servicioRepository = servicioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedEspecialidades();
        seedPacientes();
        seedMedicos();
        seedServicios();
    }

    private void seedAdmin() {
        Usuario admin = usuarioRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            admin = new Usuario();
            admin.setUsername("admin");
            admin.setRol(Rol.ADMIN);
            admin.setPassword(passwordEncoder.encode("123456"));
            usuarioRepository.save(admin);
        } else if (!esHashBcrypt(admin.getPassword())) {
            // Cuenta creada antes de agregar seguridad real: tenia la contrasena en texto plano.
            admin.setPassword(passwordEncoder.encode("123456"));
            usuarioRepository.save(admin);
        }
    }

    private boolean esHashBcrypt(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    private void seedEspecialidades() {
        String[][] datos = {
                {"Medicina General", "Atencion medica general"},
                {"Pediatria", "Atencion medica infantil"},
                {"Odontologia", "Salud dental"},
                {"Ginecologia", "Atencion de la salud reproductiva femenina"},
                {"Cardiologia", "Diagnostico y tratamiento de enfermedades del corazon"},
                {"Dermatologia", "Atencion de la piel, cabello y unas"},
                {"Laboratorio", "Analisis clinicos de orina, heces o sangre"},
                {"Enfermeria y Vacunacion", "Citas rapidas para aplicar vacunas, realizar curaciones, inyectables o controles rutinarios de presion y peso"},
                {"Rayos X", "Estudios de imagenologia por radiografia"},
        };
        for (String[] d : datos) {
            if (especialidadRepository.findByNombre(d[0]).isEmpty()) {
                especialidadRepository.save(new Especialidad(d[0], d[1]));
            }
        }
    }

    private void seedPacientes() {
        // {nombres, apellidos, dni, fechaNacimiento(ISO), telefono, email, direccion}
        String[][] datos = {
                {"Maria Fernanda", "Garcia Torres", "71234567", "1998-05-14", "987654321", "maria.garcia@gmail.com", "Av. Los Alamos 123, San Juan de Lurigancho"},
                {"Carlos Alberto", "Ramirez Quispe", "45678912", "1985-11-02", "976543210", "carlos.ramirez@gmail.com", "Jr. Las Flores 456, Comas"},
                {"Lucia Andrea", "Mendoza Rojas", "68912345", "2001-03-22", "965432198", "lucia.mendoza@gmail.com", "Calle Los Pinos 789, Ate"},
                {"Jose Manuel", "Torres Huaman", "52348761", "1979-07-30", "954321876", "jose.torres@gmail.com", "Av. Universitaria 321, Los Olivos"},
                {"Fiorella Nicole", "Vargas Salazar", "74829156", "2010-09-10", "943218765", "fiorella.vargas@gmail.com", "Jr. Amazonas 654, San Martin de Porres"},
                {"Roberto Carlos", "Flores Chavez", "41567823", "1965-01-18", "932187654", "roberto.flores@gmail.com", "Av. Proceres 987, Santa Anita"},
                {"Ana Lucia", "Fernandez Paredes", "60123478", "1992-02-19", "921456789", "ana.fernandez@gmail.com", "Av. Brasil 234, Brena"},
                {"Diego Alonso", "Salazar Mendez", "62345891", "1988-08-08", "912345678", "diego.salazar@gmail.com", "Jr. Cusco 567, Cercado de Lima"},
                {"Camila Sofia", "Rojas Delgado", "73456129", "2015-04-25", "998765432", "camila.rojas@gmail.com", "Calle Las Begonias 890, La Molina"},
                {"Martin Eduardo", "Paredes Luna", "55678234", "1975-12-01", "987123456", "martin.paredes@gmail.com", "Av. Angamos 345, Surquillo"},
                {"Valentina Isabel", "Cruz Ponce", "64789512", "2003-06-17", "976234567", "valentina.cruz@gmail.com", "Jr. Junin 678, Rimac"},
                {"Sebastian Rafael", "Guerrero Nunez", "47891234", "1996-10-09", "965345678", "sebastian.guerrero@gmail.com", "Av. Colonial 901, Callao"},
                {"Daniela Alejandra", "Chuquimango Rios", "58912456", "1990-01-27", "954456789", "daniela.chuquimango@gmail.com", "Calle Los Cedros 234, San Borja"},
                {"Andres Felipe", "Cardenas Soto", "69123567", "1982-09-14", "943567891", "andres.cardenas@gmail.com", "Av. Aviacion 456, San Luis"},
                {"Gabriela Milagros", "Injante Farfan", "71982345", "2018-03-05", "932678912", "gabriela.injante@gmail.com", "Jr. Puno 789, El Agustino"},
                {"Ricardo Manuel", "Zevallos Aguilar", "42093456", "1970-05-30", "921789123", "ricardo.zevallos@gmail.com", "Av. Tacna 123, Cercado de Lima"},
                {"Paola Estefania", "Bernal Rivas", "63104567", "1999-07-21", "910891234", "paola.bernal@gmail.com", "Calle Las Camelias 456, Surco"},
                {"Jorge Luis", "Machaca Apaza", "54215678", "1963-11-11", "998912345", "jorge.machaca@gmail.com", "Av. Pachacutec 789, Villa El Salvador"},
                {"Katherine Melissa", "Suarez Loayza", "65326789", "2006-02-14", "987923456", "katherine.suarez@gmail.com", "Jr. Ica 234, La Victoria"},
                {"Oscar Ivan", "Palacios Vega", "46437891", "1993-04-08", "976934567", "oscar.palacios@gmail.com", "Av. Argentina 567, Callao"},
                {"Milagros Pilar", "Sanchez Coronado", "57548912", "1987-08-19", "965945678", "milagros.sanchez@gmail.com", "Calle Los Jazmines 890, San Miguel"},
                {"Renzo Alexander", "Villanueva Osorio", "68659123", "2012-12-25", "954956789", "renzo.villanueva@gmail.com", "Av. La Marina 123, Pueblo Libre"},
                {"Nicole Antonella", "Reyes Bautista", "49761234", "1997-05-16", "943967891", "nicole.reyes@gmail.com", "Jr. Ancash 456, Cercado de Lima"},
                {"Hugo Alberto", "Chumpitaz Delgado", "50872345", "1958-10-02", "932978912", "hugo.chumpitaz@gmail.com", "Av. Grau 789, Barranco"},
                {"Estrella Yamileth", "Ponce Herrera", "61983456", "2020-01-30", "921989123", "estrella.ponce@gmail.com", "Calle Las Orquideas 234, Ate"},
                {"Bryan Steven", "Quiroz Maldonado", "72094567", "1991-03-13", "910991234", "bryan.quiroz@gmail.com", "Av. Canada 567, San Isidro"},
                {"Fatima Rocio", "Espinal Trujillo", "43105678", "1984-06-28", "998902345", "fatima.espinal@gmail.com", "Jr. Moquegua 890, Cercado de Lima"},
                {"Iker Matias", "Cabrera Ruiz", "64216789", "2016-09-09", "987913456", "iker.cabrera@gmail.com", "Av. Salaverry 123, Jesus Maria"},
                {"Yesenia Karol", "Huanca Mamani", "55327891", "1980-02-02", "976924567", "yesenia.huanca@gmail.com", "Calle Los Girasoles 456, Villa Maria del Triunfo"},
                {"Adrian Gustavo", "Nolasco Fuentes", "66438912", "1995-11-23", "965935678", "adrian.nolasco@gmail.com", "Av. Wilson 789, Cercado de Lima"},
        };
        for (String[] d : datos) {
            if (pacienteRepository.findByDni(d[2]).isEmpty()) {
                Paciente p = new Paciente();
                p.setNombres(d[0]);
                p.setApellidos(d[1]);
                p.setDni(d[2]);
                p.setFechaNacimiento(LocalDate.parse(d[3]));
                p.setTelefono(d[4]);
                p.setEmail(d[5]);
                p.setDireccion(d[6]);
                pacienteRepository.save(p);
            }
        }
    }

    private void seedMedicos() {
        // Contrasena inicial de demostracion para los 7 medicos de ejemplo: "medico123"
        // (el admin puede indicarles que la cambien; cambiar contrasena queda fuera de este alcance).
        // {nombres, apellidos, colegiatura, telefono, email, especialidad}
        String[][] datos = {
                {"Juan Carlos", "Espinoza Ramos", "CMP 45012", "987112233", "juan.espinoza@clinica.pe", "Medicina General"},
                {"Patricia Elena", "Salcedo Ibarra", "CMP 46789", "976223344", "patricia.salcedo@clinica.pe", "Pediatria"},
                {"Miguel Angel", "Contreras Paredes", "COP 12034", "965334455", "miguel.contreras@clinica.pe", "Odontologia"},
                {"Rosa Isabel", "Guzman Castillo", "CMP 48321", "954445566", "rosa.guzman@clinica.pe", "Ginecologia"},
                {"Fernando Jose", "Aliaga Del Rio", "CMP 50987", "943556677", "fernando.aliaga@clinica.pe", "Cardiologia"},
                {"Valeria Ximena", "Ortiz Bravo", "CMP 52456", "932667788", "valeria.ortiz@clinica.pe", "Dermatologia"},
                {"Carmen Rosa", "Huaman Ttito", "CEP 30219", "921778899", "carmen.huaman@clinica.pe", "Enfermeria y Vacunacion"},
                {"Luis Fernando", "Quezada Ramos", "TL 10456", "912334455", "laboratorio@clinica.pe", "Laboratorio"},
                {"Andrea Milagros", "Soto Ventura", "TI 20789", "923445566", "imagenologia@clinica.pe", "Rayos X"},
        };
        for (String[] d : datos) {
            // El medico y su cuenta se verifican por separado: si el medico ya existia (por ejemplo,
            // de una version anterior de este proyecto sin cuentas de acceso), igual le creamos el login.
            Medico medico = medicoRepository.findAll().stream()
                    .filter(m -> d[4].equals(m.getEmail()))
                    .findFirst()
                    .orElse(null);

            if (medico == null) {
                Especialidad especialidad = especialidadRepository.findByNombre(d[5])
                        .orElseThrow(() -> new IllegalStateException("Especialidad no encontrada: " + d[5]));
                medico = new Medico();
                medico.setNombres(d[0]);
                medico.setApellidos(d[1]);
                medico.setColegiatura(d[2]);
                medico.setTelefono(d[3]);
                medico.setEmail(d[4]);
                medico.setEspecialidad(especialidad);
                medico = medicoRepository.save(medico);
            }

            Usuario usuario = usuarioRepository.findByUsername(d[4]).orElse(null);
            if (usuario == null) {
                usuario = new Usuario();
                usuario.setUsername(d[4]);
                usuario.setRol(Rol.MEDICO);
                usuario.setMedico(medico);
                usuario.setPassword(passwordEncoder.encode("medico123"));
                usuarioRepository.save(usuario);
            } else if (!esHashBcrypt(usuario.getPassword())) {
                usuario.setPassword(passwordEncoder.encode("medico123"));
                usuarioRepository.save(usuario);
            }
        }
    }

    private void seedServicios() {
        // {nombre, precio, duracionEstimada, especialidad}
        Object[][] datos = {
                {"Consulta de Medicina General", "30", null, "Medicina General"},
                {"Consulta de Pediatria", "50", null, "Pediatria"},
                {"Consulta de Odontologia", "50", null, "Odontologia"},
                {"Consulta de Ginecologia", "100", null, "Ginecologia"},
                {"Consulta de Cardiologia", "120", null, "Cardiologia"},
                {"Consulta de Dermatologia", "80", null, "Dermatologia"},
                {"Radiografia", "80", null, "Rayos X"},
                {"Inyectable", "10", null, "Enfermeria y Vacunacion"},
                {"Toma de presion", "5", null, "Enfermeria y Vacunacion"},
                {"Parasitologico (3 muestras)", "40", null, "Laboratorio"},
                {"Coprocultivo", "90", "5 dias habiles", "Laboratorio"},
                {"Reaccion inflamatoria en heces", "40", null, "Laboratorio"},
                {"Hemograma", "50", null, "Laboratorio"},
                {"PCR", "30", null, "Laboratorio"},
                {"Perfil lipidico", "60", null, "Laboratorio"},
                {"Perfil hepatico", "60", null, "Laboratorio"},
                {"Urea", "30", null, "Laboratorio"},
                {"Creatinina", "30", null, "Laboratorio"},
                {"Acido urico", "40", null, "Laboratorio"},
                {"Urocultivo", "80", "5 dias habiles", "Laboratorio"},
                {"Orina completa", "30", "2 horas", "Laboratorio"},
        };

        for (Object[] d : datos) {
            String nombre = (String) d[0];
            String nombreEspecialidad = (String) d[3];
            Especialidad especialidad = especialidadRepository.findByNombre(nombreEspecialidad)
                    .orElseThrow(() -> new IllegalStateException("Especialidad no encontrada: " + nombreEspecialidad));

            if (servicioRepository.findByNombreAndEspecialidadId(nombre, especialidad.getId()).isPresent()) {
                continue;
            }

            Servicio servicio = new Servicio();
            servicio.setNombre(nombre);
            servicio.setPrecio(new BigDecimal((String) d[1]));
            servicio.setDuracionEstimada((String) d[2]);
            servicio.setEspecialidad(especialidad);
            servicioRepository.save(servicio);
        }
    }
}
