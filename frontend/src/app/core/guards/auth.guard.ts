import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

function guardConRoles(roles: string[]): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (!authService.estaAutenticado()) {
      router.navigate(['/login']);
      return false;
    }

    if (roles.length > 0 && !authService.tieneRol(...roles)) {
      router.navigate([authService.rutaInicioSegunRol()]);
      return false;
    }

    return true;
  };
}

/** Cualquier usuario con sesion iniciada, sin importar el rol. */
export const authGuard: CanActivateFn = guardConRoles([]);

/** Solo personal de la clinica (panel de administracion). */
export const adminGuard: CanActivateFn = guardConRoles(['ADMIN', 'RECEPCIONISTA']);

/** Solo medicos (su agenda). */
export const medicoGuard: CanActivateFn = guardConRoles(['MEDICO']);

/** Solo pacientes (agendar/ver sus citas). */
export const pacienteGuard: CanActivateFn = guardConRoles(['PACIENTE']);
