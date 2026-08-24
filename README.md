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
