import { Paciente } from './paciente.model';
import { Medico } from './medico.model';
import { Servicio } from './servicio.model';

export type EstadoCita = 'PENDIENTE' | 'CONFIRMADA' | 'ATENDIDA' | 'CANCELADA';

export interface Cita {
  id?: number;
  paciente: Paciente;
  medico: Medico;
  servicio?: Servicio;
  fecha: string;
  hora: string;
  motivo?: string;
  estado?: EstadoCita;
  pagado?: boolean;
  montoPagado?: number;
  metodoPago?: string;
  fechaPago?: string;
}
