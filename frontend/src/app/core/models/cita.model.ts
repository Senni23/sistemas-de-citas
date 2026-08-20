import { Paciente } from './paciente.model';
import { Medico } from './medico.model';

export type EstadoCita = 'PENDIENTE' | 'CONFIRMADA' | 'ATENDIDA' | 'CANCELADA';

export interface Cita {
  id?: number;
  paciente: Paciente;
  medico: Medico;
  fecha: string;
  hora: string;
  motivo?: string;
  estado?: EstadoCita;
}
