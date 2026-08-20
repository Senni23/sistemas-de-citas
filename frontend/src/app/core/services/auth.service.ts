import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SesionUsuario {
  id: number;
  username: string;
  rol: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private readonly storageKey = 'citas_usuario';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<SesionUsuario> {
    return this.http
      .post<SesionUsuario>(`${this.baseUrl}/login`, { username, password })
      .pipe(tap((usuario) => localStorage.setItem(this.storageKey, JSON.stringify(usuario))));
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
  }

  usuarioActual(): SesionUsuario | null {
    const raw = localStorage.getItem(this.storageKey);
    return raw ? (JSON.parse(raw) as SesionUsuario) : null;
  }

  estaAutenticado(): boolean {
    return this.usuarioActual() !== null;
  }
}
