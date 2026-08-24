import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Servicio } from '../models/servicio.model';

@Injectable({ providedIn: 'root' })
export class ServicioService {
  private readonly baseUrl = `${environment.apiUrl}/servicios`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(this.baseUrl);
  }

  listarPorEspecialidad(especialidadId: number): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(this.baseUrl, { params: { especialidadId } });
  }
}
