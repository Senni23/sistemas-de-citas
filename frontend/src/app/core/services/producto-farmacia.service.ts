import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProductoFarmacia } from '../models/producto-farmacia.model';

@Injectable({ providedIn: 'root' })
export class ProductoFarmaciaService {
  private readonly baseUrl = `${environment.apiUrl}/farmacia/productos`;

  constructor(private http: HttpClient) {}

  listar(): Observable<ProductoFarmacia[]> {
    return this.http.get<ProductoFarmacia[]>(this.baseUrl);
  }

  crear(producto: ProductoFarmacia): Observable<ProductoFarmacia> {
    return this.http.post<ProductoFarmacia>(this.baseUrl, producto);
  }

  actualizar(id: number, producto: ProductoFarmacia): Observable<ProductoFarmacia> {
    return this.http.put<ProductoFarmacia>(`${this.baseUrl}/${id}`, producto);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
