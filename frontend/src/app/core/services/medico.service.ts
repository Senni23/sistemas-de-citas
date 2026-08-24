import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Medico, MedicoRegistro } from '../models/medico.model';

@Injectable({ providedIn: 'root' })
export class MedicoService {
  private readonly baseUrl = `${environment.apiUrl}/medicos`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Medico[]> {
    return this.http.get<Medico[]>(this.baseUrl);
  }

  obtener(id: number): Observable<Medico> {
    return this.http.get<Medico>(`${this.baseUrl}/${id}`);
  }

  crear(medico: MedicoRegistro): Observable<Medico> {
    return this.http.post<Medico>(this.baseUrl, medico);
  }

  actualizar(id: number, medico: Medico): Observable<Medico> {
    return this.http.put<Medico>(`${this.baseUrl}/${id}`, medico);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
