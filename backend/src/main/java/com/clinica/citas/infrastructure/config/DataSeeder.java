package com.clinica.citas.infrastructure.config;

import com.clinica.citas.domain.catalogo.Especialidad;
import com.clinica.citas.domain.catalogo.EspecialidadRepository;
import com.clinica.citas.domain.catalogo.Servicio;
import com.clinica.citas.domain.catalogo.ServicioRepository;
import com.clinica.citas.domain.farmacia.ProductoFarmacia;
import com.clinica.citas.domain.farmacia.ProductoFarmaciaRepository;
import com.clinica.citas.domain.farmacia.Stock;
import com.clinica.citas.domain.identidad.PasswordHasher;
import com.clinica.citas.domain.identidad.Usuario;
import com.clinica.citas.domain.identidad.UsuarioRepository;
import com.clinica.citas.domain.medicos.Colegiatura;
import com.clinica.citas.domain.medicos.Medico;
import com.clinica.citas.domain.medicos.MedicoRepository;
import com.clinica.citas.domain.pacientes.Dni;
import com.clinica.citas.domain.pacientes.Paciente;
import com.clinica.citas.domain.pacientes.PacienteRepository;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.Dinero;
import com.clinica.citas.domain.shared.NombrePersona;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Carga datos de ejemplo al arrancar. Trabaja con los agregados y los repositorios del dominio (no con tablas).
 * Cada metodo es idempotente: si el dato ya existe, no lo vuelve a insertar.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final EspecialidadRepository especialidades;
    private final MedicoRepository medicos;
    private final PacienteRepository pacientes;
    private final ServicioRepository servicios;
    private final ProductoFarmaciaRepository productos;
    private final PasswordHasher hasher;

    public DataSeeder(UsuarioRepository usuarios, EspecialidadRepository especialidades, MedicoRepository medicos,
                      PacienteRepository pacientes, ServicioRepository servicios, ProductoFarmaciaRepository productos,
                      PasswordHasher hasher) {
        this.usuarios = usuarios;
        this.especialidades = especialidades;
        this.medicos = medicos;
        this.pacientes = pacientes;
        this.servicios = servicios;
        this.productos = productos;
        this.hasher = hasher;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedEspecialidades();
        seedPacientes();
        seedMedicos();
        seedServicios();
        seedFarmacia();
    }

    private void seedAdmin() {
        Optional<Usuario> existente = usuarios.buscarPorUsername("admin");
        if (existente.isEmpty()) {
            usuarios.guardar(Usuario.administrador("admin", hasher.cifrar("123456")));
        } else if (!existente.get().passwordHash().esBcrypt()) {
            // Cuenta creada antes de agregar seguridad real: tenia la contrasena en texto plano.
            existente.get().cambiarPassword(hasher.cifrar("123456"));
            usuarios.guardar(existente.get());
        }
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
            if (especialidades.buscarPorNombre(d[0]).isEmpty()) {
                especialidades.guardar(Especialidad.crear(d[0], d[1]));
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
            Dni dni = Dni.de(d[2]);
            if (pacientes.buscarPorDni(dni).isEmpty()) {
                pacientes.guardar(Paciente.registrar(new NombrePersona(d[0], d[1]), dni, LocalDate.parse(d[3]), d[4],
                        CorreoElectronico.de(d[5]), d[6]));
            }
        }
    }

    private void seedMedicos() {
        // Contrasena inicial de demostracion para los medicos de ejemplo: "medico123"
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
            // El medico y su cuenta se verifican por separado: si el medico ya existia (por ejemplo, de una
            // version anterior del proyecto sin cuentas de acceso), igual le creamos el login.
            Medico medico = medicos.buscarPorCorreo(d[4]).orElse(null);
            if (medico == null) {
                Especialidad especialidad = especialidades.buscarPorNombre(d[5])
                        .orElseThrow(() -> new IllegalStateException("Especialidad no encontrada: " + d[5]));
                medico = medicos.guardar(Medico.registrar(new NombrePersona(d[0], d[1]), Colegiatura.de(d[2]), d[3],
                        CorreoElectronico.de(d[4]), especialidad.id()));
            }
            Optional<Usuario> cuenta = usuarios.buscarPorUsername(d[4]);
            if (cuenta.isEmpty()) {
                usuarios.guardar(Usuario.paraMedico(d[4], hasher.cifrar("medico123"), medico.id()));
            } else if (!cuenta.get().passwordHash().esBcrypt()) {
                cuenta.get().cambiarPassword(hasher.cifrar("medico123"));
                usuarios.guardar(cuenta.get());
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
            Especialidad especialidad = especialidades.buscarPorNombre(nombreEspecialidad)
                    .orElseThrow(() -> new IllegalStateException("Especialidad no encontrada: " + nombreEspecialidad));
            if (servicios.buscarPorNombreYEspecialidad(nombre, especialidad.id()).isPresent()) {
                continue;
            }
            servicios.guardar(Servicio.crear(nombre, new Dinero(new BigDecimal((String) d[1])), (String) d[2], especialidad.id()));
        }
    }

    private void seedFarmacia() {
        // {nombre, descripcion, stock, stockMinimo, unidadMedida, precioUnitario, fechaVencimiento(ISO, puede ser null)}
        Object[][] datos = {
                {"Paracetamol 500mg", "Analgesico y antipirético, caja x 100 tabletas", 40, 20, "caja", "8.50", "2027-06-30"},
                {"Ibuprofeno 400mg", "Antiinflamatorio, caja x 100 tabletas", 35, 20, "caja", "9.80", "2027-03-15"},
                {"Amoxicilina 500mg", "Antibiotico, caja x 100 capsulas", 15, 15, "caja", "22.00", "2026-11-20"},
                {"Guantes de latex talla M", "Guantes de examen descartables, caja x 100 unidades", 25, 10, "caja", "18.00", null},
                {"Jeringas 5ml", "Jeringas descartables con aguja, paquete x 100 unidades", 50, 25, "paquete", "35.00", null},
                {"Gasas esteriles", "Gasas 10x10cm, paquete x 50 unidades", 8, 15, "paquete", "12.50", null},
                {"Alcohol antiseptico 70%", "Frasco de 1 litro", 30, 10, "frasco", "6.00", "2027-09-01"},
                {"Suero fisiologico 500ml", "Solucion salina para irrigacion", 20, 10, "unidad", "4.50", "2027-01-10"},
                {"Mascarillas quirurgicas", "Caja x 50 unidades", 60, 20, "caja", "15.00", null},
                {"Termometro digital", "Termometro digital de uso clinico", 12, 5, "unidad", "20.00", null},
        };
        for (Object[] d : datos) {
            String nombre = (String) d[0];
            if (productos.buscarPorNombre(nombre).isPresent()) {
                continue;
            }
            productos.guardar(ProductoFarmacia.registrar(nombre, (String) d[1], Stock.de((Integer) d[2], (Integer) d[3]),
                    (String) d[4], new Dinero(new BigDecimal((String) d[5])),
                    d[6] != null ? LocalDate.parse((String) d[6]) : null));
        }
    }
}
