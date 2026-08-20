import { Especialidad } from './especialidad.model';

export interface Medico {
  id?: number;
  nombres: string;
  apellidos: string;
  colegiatura: string;
  telefono?: string;
  email?: string;
  especialidad: Especialidad;
}
