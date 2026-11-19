# Sistema de Gestion de Citas Medicas

Proyecto final del curso **Herramientas de Desarrollo**. Sistema web para gestionar citas de un
consultorio/clinica: pacientes, medicos, especialidades y agenda de citas.

## Stack tecnologico

- **Backend:** Java 21, Spring Boot 3, Spring Data JPA, MySQL, Maven
- **Frontend:** Angular 17 (standalone components), Bootstrap 5
- **Control de versiones:** Git + GitHub (Issues, Projects/Kanban, Pull Requests)
- **CI/CD:** GitHub Actions
- **Despliegue:** Docker + Docker Compose

## Estructura del repositorio

```
SistemaCitasMedicas/
├── backend/           # API REST en Spring Boot
├── frontend/           # Aplicacion Angular
├── docs/
│   ├── manual-github/  # Manual de uso de Git/GitHub del equipo
│   └── site/            # Pagina de documentacion publicada en GitHub Pages
├── .github/workflows/   # Workflows de CI/CD
└── docker-compose.yml
```

## Roles y portales

El sistema tiene tres experiencias separadas segun el rol de la cuenta, protegidas con **Spring Security + JWT**
(contrasenas hasheadas con BCrypt, ningun endpoint de datos es publico):

| Rol | Como obtiene su cuenta | Que puede hacer |
|---|---|---|
| **ADMIN / RECEPCIONISTA** | Ya existen (usuario `admin`) | Panel completo: pacientes, medicos, especialidades, todas las citas, dashboard |
| **MEDICO** | El admin la crea desde la pantalla "Medicos" (usuario = su correo) | Ver su agenda propia por fecha, confirmar/atender/cancelar sus citas |
| **PACIENTE** | Se registra el mismo en `/registro` | Agendar citas (asistente de 3 pasos), ver y cancelar sus propias citas |

Cada rol solo puede ver y modificar lo suyo: un paciente no puede ver citas de otro paciente, un medico no puede ver
la agenda de otro medico, y ninguno de los dos puede entrar al panel de administracion.

## Modulos del sistema

1. Autenticacion (login, registro de pacientes) con JWT
2. Pacientes (CRUD, solo personal de la clinica)
3. Medicos y especialidades (CRUD, solo personal de la clinica)
4. Citas (agendar, cambiar estado, cancelar) con reglas de acceso por rol
5. Dashboard con resumen general (solo personal de la clinica)
6. Portal del paciente: registro y asistente para agendar citas
7. Portal del medico: agenda propia agrupada por fecha

## Arquitectura del backend (Domain-Driven Design)

El backend esta organizado en cuatro capas. Cada capa solo depende de las que estan "hacia adentro":
`interfaces` → `application` → `domain` ← `infrastructure`.

```
backend/src/main/java/com/clinica/citas/
├── domain/           # El negocio, sin Spring ni JPA
│   ├── citas/        #   Cita (raiz de agregado), HorarioCita, PagoCita, DisponibilidadMedico, CitaRepository (contrato)
│   ├── pacientes/    #   Paciente, Dni, PacienteRepository
│   ├── medicos/      #   Medico, Colegiatura, MedicoRepository
│   ├── catalogo/     #   Especialidad, Servicio y sus repositorios
│   ├── identidad/    #   Usuario, Rol, PasswordHash, TokenRecuperacion, PasswordHasher (puerto)
│   ├── farmacia/     #   ProductoFarmacia, Stock, ProductoFarmaciaRepository
│   └── shared/       #   Dinero, NombrePersona, CorreoElectronico, excepciones del dominio
├── application/      # Casos de uso (CQRS): comandos, consultas y handlers + vistas (DTO de salida)
│   └── <contexto>/   #   command/ query/ handler/ view/   (ej. citas/handler/AgendarCitaHandler)
├── infrastructure/   # Detalles tecnicos que implementan los contratos del dominio
│   ├── persistence/  #   entity/ (JPA) · mapper/ · springdata/ · repository/ (implementan los contratos)
│   ├── security/     #   JWT, BCrypt, Spring Security
│   ├── email/        #   Envio de correo (SMTP)
│   └── config/       #   Tomcat, reloj, servicios de dominio, DataSeeder
└── interfaces/rest/  # Controladores REST, requests (entrada) y manejo de errores
```

- **Comandos** cambian datos (agendar, pagar, registrar...); **consultas** solo leen (listar, obtener...). Cada uno tiene su *handler*.
- El **dominio** valida sus propias reglas (horario de atencion, cruce de horarios, token de recuperacion de 1 hora...).
- Los agregados se referencian por **id** (`Cita` guarda `PacienteId`, `MedicoId`...), no por objeto.
- La API REST mantiene las mismas rutas y el mismo formato JSON, asi que el frontend no cambia.

### Persistencia con JPA y consultas

- **Relaciones**: las entidades JPA declaran `@ManyToOne` (cita → paciente, medico y servicio; medico y servicio → especialidad), `@OneToOne` (usuario → medico o paciente) y `@OneToMany` en el lado inverso. Como el dominio solo conoce ids, el id se escribe por su columna y la asociacion es de **solo lectura** (`insertable = false, updatable = false`): asi MySQL crea y hace cumplir las llaves foraneas y las consultas pueden hacer JOIN.
- **Restricciones**: `unique = true` en DNI, colegiatura y correo, y `@UniqueConstraint` para que un servicio no se repita en su especialidad.
- **Consultas** (`CitaJpaRepository`): por nombre de metodo (`findBy…`, `countBy…`), JPQL con `@Query` y `@Param` (cruce de horarios, agenda del medico con `Sort`, busqueda con `Pageable`, reporte por especialidad con JOIN) y una consulta nativa en SQL (citas por estado).
- **Paginacion sin depender de Spring Data**: el dominio define `Pagina` y `SolicitudDePagina`; la infraestructura los traduce a `Pageable`.
- **Reportes** del tablero: el puerto `ReportesDeCitas` (aplicacion) lo implementa `ReportesDeCitasJpa` (infraestructura).

### API REST: respuestas y paginacion

- Los `POST` de creacion responden **201 Created** con la cabecera **`Location`** (por ejemplo `/api/citas/15`); CORS la deja expuesta para el frontend. Los `DELETE` responden 204.
- `GET /api/citas/buscar` (solo personal de la clinica) lista de a una pagina. Parametros opcionales: `estado` (PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA), `orden` (FECHA, ESTADO o ID), `descendente` (true/false), `pagina` (desde 0) y `tamano` (1 a 100). Ejemplo: `/api/citas/buscar?estado=PENDIENTE&orden=FECHA&descendente=true&pagina=0&tamano=10`.
- `GET /api/dashboard/resumen` incluye, ademas de los totales, `citasPorEstado` y `citasPorEspecialidad`.
- Eliminar un paciente o un medico que todavia tiene citas responde **409** (lo impide la llave foranea).

### Pruebas y cobertura

`mvn clean verify` ejecuta las 145 pruebas (dominio, casos de uso, infraestructura sobre H2 e integracion de la API) y genera el reporte de cobertura de JaCoCo en `backend/target/site/jacoco/index.html` (87,5 % de lineas).

## Como ejecutar en local (desarrollo)

### Requisitos

- Java 21+ (NetBeans trae Maven incluido, no hace falta instalarlo aparte)
- Node.js 20 y npm (para el frontend)
- MySQL corriendo en el puerto 3306 con usuario `root` (ajustar `backend/src/main/resources/application.properties` si tu instalacion usa otro usuario/password/puerto)

### Backend (NetBeans)

1. `File > Open Project` y selecciona la carpeta `backend` (NetBeans la reconoce por el `pom.xml`).
2. Click derecho sobre el proyecto > `Run` (la primera vez descarga las dependencias de Maven).
3. La API queda disponible en `http://localhost:8080/api`.

Alternativa por consola, si tienes Maven en el PATH:

```bash
cd backend
mvn spring-boot:run
```

### Frontend (VS Code)

1. Instala Node.js LTS desde nodejs.org si no lo tienes.
2. Abre la carpeta `frontend` en VS Code.
3. En la terminal integrada:

```bash
cd frontend
npm install
npm start
```

La aplicacion queda disponible en `http://localhost:4200` (usa un proxy hacia el backend, ver `proxy.conf.json`).

### Usuarios de prueba

Se cargan automaticamente al arrancar el backend por primera vez (ver `DataSeeder.java`):

- **Admin:** `admin` / `123456`
- **Medico** (cualquiera de los 7 sembrados, ej. `juan.espinoza@clinica.pe`) / `medico123`
- **Paciente:** no viene precargado con contrasena (los 30 pacientes de ejemplo son solo datos, sin cuenta) —
  registra uno nuevo desde `/registro` para probar ese flujo.

### Variables de entorno / secretos

El repositorio **no** incluye contrasenas reales. En desarrollo local, crea el archivo
`backend/src/main/resources/application-local.properties` (esta en `.gitignore`, nunca se sube) con:

```properties
spring.datasource.password=TU_PASSWORD_DE_MYSQL
app.jwt.secret=cualquier-texto-largo-y-aleatorio-de-al-menos-32-caracteres
```

En otros entornos (CI, servidor), define en su lugar las variables `DB_PASSWORD` y `JWT_SECRET`.

## Como ejecutar con Docker

Requiere tener Docker Desktop instalado.

```bash
docker compose up --build
```

Esto levanta 3 contenedores: `mysql` (puerto 3307), `backend` (puerto 8080) y `frontend` (puerto 4200).

## CI/CD

Este repositorio usa GitHub Actions para:

- Validar la estructura del proyecto (`ci-01-estructura.yml`)
- Compilar el backend con Maven (`ci-02-backend.yml`)
- Compilar el frontend con Angular (`ci-03-frontend.yml`)
- Build completo del sistema (`ci-04-build-completo.yml`)
- Desplegar la pagina de documentacion en GitHub Pages (`cd-01-github-pages.yml`)

## Equipo

Completar con los integrantes del proyecto.
