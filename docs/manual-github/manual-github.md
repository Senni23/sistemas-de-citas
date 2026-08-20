# Manual de Gestion de GitHub del Equipo

Este manual describe como el equipo debe trabajar con Git y GitHub durante el desarrollo del
Sistema de Gestion de Citas Medicas.

## 1. Flujo de ramas (branching)

- `main`: rama principal, siempre debe quedar estable y compilando.
- `feature/<modulo>`: una rama por cada modulo o tarea, por ejemplo:
  - `feature/modulo-pacientes`
  - `feature/modulo-medicos`
  - `feature/modulo-citas`
  - `feature/ci-cd`

Nunca se trabaja directamente sobre `main`.

## 2. Flujo de trabajo diario

1. Actualizar `main` local: `git pull origin main`
2. Crear una rama para la tarea: `git checkout -b feature/modulo-pacientes`
3. Hacer commits pequenos y descriptivos:
   ```bash
   git add .
   git commit -m "feat: agregar CRUD de pacientes en el backend"
   ```
4. Subir la rama: `git push origin feature/modulo-pacientes`
5. Abrir un **Pull Request** hacia `main` en GitHub.
6. Al menos un integrante revisa el PR antes de aprobarlo (`Merge`).
7. Eliminar la rama despues del merge.

## 3. Convencion de mensajes de commit

Se usa un prefijo segun el tipo de cambio:

| Prefijo    | Uso                                             |
|------------|--------------------------------------------------|
| `feat:`    | Nueva funcionalidad                              |
| `fix:`     | Correccion de errores                            |
| `docs:`    | Cambios en documentacion                         |
| `ci:`      | Cambios en workflows de GitHub Actions           |
| `refactor:`| Cambios internos sin alterar el comportamiento   |

## 4. Issues y tablero Kanban

Cada tarea del proyecto se registra como un **Issue** en GitHub, con:

- Titulo claro (ej. "Implementar modulo de citas - backend")
- Descripcion de lo que se debe hacer
- Etiqueta (`backend`, `frontend`, `ci-cd`, `docs`)
- Responsable asignado

El tablero Kanban (GitHub Projects) tiene las columnas:

- **Backlog**: tareas planificadas
- **En progreso**: tareas que se estan desarrollando
- **En revision**: Pull Requests abiertos, pendientes de revision
- **Finalizado**: tareas completadas y fusionadas a `main`

## 5. Pull Requests

- Todo cambio a `main` se hace mediante un Pull Request, nunca con push directo.
- El PR debe describir que modulo o funcionalidad implementa y, si aplica, enlazar el Issue
  relacionado (`Closes #12`).
- El PR debe pasar los workflows de CI (compilacion de backend y frontend) antes de fusionarse.

## 6. Buenas practicas

- No subir archivos generados (`target/`, `node_modules/`, `dist/`) — ya estan en `.gitignore`.
- Mantener el `README.md` actualizado con instrucciones de ejecucion.
- Revisar la pestana **Actions** despues de cada push para verificar que el CI paso correctamente.
