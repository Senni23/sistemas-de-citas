import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SesionUsuario {
  token: string;
  username: string;
  rol: 'ADMIN' | 'RECEPCIONISTA' | 'MEDICO' | 'PACIENTE';
  nombre: string;
  entidadId: number | null;
}

export interface RegistroPaciente {
  nombres: string;
  apellidos: string;
  dni: string;
  telefono: string;
  email: string;
  direccion: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private readonly storageKey = 'citas_usuario';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<SesionUsuario> {
    return this.http
      .post<SesionUsuario>(`${this.baseUrl}/login`, { username, password })
      .pipe(tap((usuario) => this.guardarSesion(usuario)));
  }

  registrarPaciente(datos: RegistroPaciente): Observable<SesionUsuario> {
    return this.http
      .post<SesionUsuario>(`${this.baseUrl}/registro`, datos)
      .pipe(tap((usuario) => this.guardarSesion(usuario)));
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
  }

  usuarioActual(): SesionUsuario | null {
    const raw = localStorage.getItem(this.storageKey);
    return raw ? (JSON.parse(raw) as SesionUsuario) : null;
  }

  token(): string | null {
    return this.usuarioActual()?.token ?? null;
  }

  estaAutenticado(): boolean {
    return this.usuarioActual() !== null;
  }

  tieneRol(...roles: string[]): boolean {
    const usuario = this.usuarioActual();
    return !!usuario && roles.includes(usuario.rol);
  }

  rutaInicioSegunRol(): string {
    const usuario = this.usuarioActual();
    switch (usuario?.rol) {
      case 'MEDICO':
        return '/mi-agenda';
      case 'PACIENTE':
        return '/mis-citas';
      default:
        return '/dashboard';
    }
  }

  private guardarSesion(usuario: SesionUsuario): void {
    localStorage.setItem(this.storageKey, JSON.stringify(usuario));
  }
}
