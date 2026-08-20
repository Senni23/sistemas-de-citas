# Definicion del Proyecto (insumo para el Capitulo 1 del informe)

## Titulo del proyecto

Sistema de Gestion de Citas Medicas para un consultorio/clinica.

## 1.1 Descripcion del problema

Los consultorios y clinicas pequenas suelen gestionar sus citas de forma manual (cuadernos,
llamadas telefonicas, mensajes de WhatsApp), lo que genera duplicidad de horarios, perdida de
informacion de pacientes, dificultad para llevar un historial ordenado de citas por paciente y
poca visibilidad sobre la carga de trabajo de cada medico. Esto afecta la atencion al paciente y
dificulta la administracion del consultorio.

## 1.2 Objetivos

### 1.2.1 Objetivo general

Disenar e implementar un sistema web para la gestion de citas medicas que permita administrar
pacientes, medicos, especialidades y la agenda de citas, optimizando el proceso de programacion,
reduciendo errores de agenda y centralizando la informacion del consultorio.

### 1.2.2 Objetivos especificos

- Analizar el proceso actual de agendamiento de citas para identificar los requerimientos del sistema.
- Implementar un repositorio en GitHub para centralizar y controlar el codigo fuente del proyecto.
- Aplicar Git como sistema de control de versiones para registrar y respaldar cada cambio del equipo.
- Implementar un modulo de gestion de pacientes con operaciones CRUD.
- Implementar un modulo de gestion de medicos y especialidades.
- Implementar un modulo de citas que permita agendar, reprogramar, cambiar de estado y cancelar citas.
- Disenar un dashboard que resuma la informacion principal del consultorio.
- Automatizar la validacion del proyecto (build de backend y frontend) mediante GitHub Actions (CI).
- Automatizar el despliegue de la documentacion del proyecto mediante GitHub Pages (CD).
- Desplegar el sistema completo (backend, frontend y base de datos) usando Docker.

### 1.2.3 Alcances y limitaciones

**Alcances**

- Registro y administracion de pacientes, medicos, especialidades y citas.
- Agenda de citas con estados: pendiente, confirmada, atendida, cancelada.
- Dashboard con resumen de pacientes, medicos y citas del dia.
- Backend desarrollado en Spring Boot con API REST, frontend en Angular.
- Control de versiones y trabajo colaborativo mediante GitHub (Issues, Kanban, Pull Requests).
- Automatizacion de compilacion (CI) y despliegue de documentacion (CD) con GitHub Actions.
- Despliegue del sistema completo mediante Docker y docker-compose.

**Limitaciones**

- No incluye modulo de facturacion o pagos.
- No incluye notificaciones automaticas por SMS o correo electronico en esta primera version.
- No maneja historia clinica detallada (diagnosticos, examenes), solo un campo de motivo de consulta.
- La autenticacion es basica (usuario y contrasena en base de datos), sin JWT ni doble factor,
  suficiente para el alcance academico del curso.
- El despliegue en GitHub Pages solo publica la documentacion del proyecto, ya que el sistema
  requiere un backend y una base de datos que no pueden ejecutarse en un hosting estatico.

## Modulos y reparto sugerido entre el equipo (2 a 5 integrantes)

| Modulo                              | Incluye                                                              |
|--------------------------------------|-----------------------------------------------------------------------|
| Estructura base + CI/CD              | Estructura del repo, workflows de GitHub Actions, Docker              |
| Pacientes                            | Entidad, repositorio, servicio, controlador (backend) + CRUD (frontend) |
| Medicos y especialidades             | Entidad, repositorio, servicio, controlador (backend) + CRUD (frontend) |
| Citas                                 | Entidad, repositorio, servicio, controlador (backend) + agenda (frontend) |
| Autenticacion + Dashboard             | Login, guard de rutas, endpoint de resumen, pantalla de dashboard      |

## Planificacion sugerida (8 semanas / 3 sprints)

- **Sprint 1 (semanas 1-2):** estructura del repo, configuracion de Git/GitHub, modelo de datos,
  conexion a MySQL, CI basico (validacion de estructura).
- **Sprint 2 (semanas 3-5):** modulos de pacientes, medicos, especialidades y citas (backend + frontend),
  CI de compilacion de backend y frontend.
- **Sprint 3 (semanas 6-8):** dashboard, autenticacion, Docker y docker-compose, CD a GitHub Pages,
  pruebas finales, manual de usuario y redaccion del informe.
