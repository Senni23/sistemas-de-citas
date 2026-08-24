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

/** Payload para crear un medico nuevo: tambien crea su cuenta de acceso. */
export interface MedicoRegistro {
  nombres: string;
  apellidos: string;
  colegiatura: string;
  telefono?: string;
  email: string;
  especialidadId: number;
  password: string;
}
