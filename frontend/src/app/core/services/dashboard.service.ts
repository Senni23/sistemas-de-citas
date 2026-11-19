import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EstadoCita } from '../models/cita.model';

export interface CitasPorEstado {
  estado: EstadoCita;
  total: number;
}

export interface CitasPorEspecialidad {
  especialidad: string;
  total: number;
}

export interface ResumenDashboard {
  totalPacientes: number;
  totalMedicos: number;
  totalCitas: number;
  citasHoy: number;
  citasPorEstado: CitasPorEstado[];
  citasPorEspecialidad: CitasPorEspecialidad[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly baseUrl = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  resumen(): Observable<ResumenDashboard> {
    return this.http.get<ResumenDashboard>(`${this.baseUrl}/resumen`);
  }
}
