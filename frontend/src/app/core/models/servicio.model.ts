import { Especialidad } from './especialidad.model';

export interface Servicio {
  id?: number;
  nombre: string;
  precio: number;
  duracionEstimada?: string;
  especialidad: Especialidad;
}
