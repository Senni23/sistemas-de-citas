import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cita, EstadoCita } from '../models/cita.model';

@Injectable({ providedIn: 'root' })
export class CitaService {
  private readonly baseUrl = `${environment.apiUrl}/citas`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Cita[]> {
    return this.http.get<Cita[]>(this.baseUrl);
  }

  listarPorPaciente(pacienteId: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(`${this.baseUrl}/paciente/${pacienteId}`);
  }

  listarPorMedico(medicoId: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(`${this.baseUrl}/medico/${medicoId}`);
  }

  obtener(id: number): Observable<Cita> {
    return this.http.get<Cita>(`${this.baseUrl}/${id}`);
  }

  crear(cita: Cita): Observable<Cita> {
    return this.http.post<Cita>(this.baseUrl, cita);
  }

  actualizar(id: number, cita: Cita): Observable<Cita> {
    return this.http.put<Cita>(`${this.baseUrl}/${id}`, cita);
  }

  cambiarEstado(id: number, estado: EstadoCita): Observable<Cita> {
    return this.http.patch<Cita>(`${this.baseUrl}/${id}/estado`, { estado });
  }

  pagar(id: number, metodoPago: string): Observable<Cita> {
    return this.http.post<Cita>(`${this.baseUrl}/${id}/pagar`, { metodoPago });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
