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

## Modulos del sistema

1. Autenticacion (login)
2. Pacientes (CRUD)
3. Medicos y especialidades (CRUD)
4. Citas (agendar, cambiar estado, cancelar)
5. Dashboard con resumen general

## Como ejecutar en local (desarrollo)

### Requisitos

- Java 21 y Maven
- Node.js 20 y npm
- MySQL 8 corriendo en el puerto 3307 (o ajustar `backend/src/main/resources/application.properties`)

### Backend

```bash
cd backend
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080/api`.

### Frontend

```bash
cd frontend
npm install
npm start
```

La aplicacion queda disponible en `http://localhost:4200` (usa un proxy hacia el backend, ver `proxy.conf.json`).

### Usuario de prueba

- Usuario: `admin`
- Contrasena: `123456`

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
