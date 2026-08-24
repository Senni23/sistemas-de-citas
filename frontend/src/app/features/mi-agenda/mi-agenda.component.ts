import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Cita, EstadoCita } from '../../core/models/cita.model';
import { CitaService } from '../../core/services/cita.service';
import { AuthService } from '../../core/services/auth.service';

interface GrupoFecha {
  fecha: string;
  esHoy: boolean;
  citas: Cita[];
}

@Component({
  selector: 'app-mi-agenda',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mi-agenda.component.html',
})
export class MiAgendaComponent implements OnInit {
  grupos: GrupoFecha[] = [];
  cargando = true;
  nombreMedico = '';

  constructor(private citaService: CitaService, private authService: AuthService) {}

  ngOnInit(): void {
    const usuario = this.authService.usuarioActual();
    this.nombreMedico = usuario?.nombre ?? '';
    const medicoId = usuario?.entidadId;
    if (!medicoId) {
      this.cargando = false;
      return;
    }
    this.citaService.listarPorMedico(medicoId).subscribe((data) => {
      this.grupos = this.agrupar(data);
      this.cargando = false;
    });
  }

  private agrupar(citas: Cita[]): GrupoFecha[] {
    const hoy = new Date().toISOString().split('T')[0];
    const ordenadas = [...citas]
      .filter((c) => c.estado !== 'CANCELADA')
      .sort((a, b) => (a.fecha + a.hora).localeCompare(b.fecha + b.hora));

    const mapa = new Map<string, Cita[]>();
    for (const c of ordenadas) {
      if (!mapa.has(c.fecha)) {
        mapa.set(c.fecha, []);
      }
      mapa.get(c.fecha)!.push(c);
    }

    return Array.from(mapa.entries()).map(([fecha, citasDelDia]) => ({
      fecha,
      esHoy: fecha === hoy,
      citas: citasDelDia,
    }));
  }

  cambiarEstado(cita: Cita, estado: EstadoCita): void {
    if (!cita.id) {
      return;
    }
    this.citaService.cambiarEstado(cita.id, estado).subscribe(() => {
      cita.estado = estado;
    });
  }

  claseEstado(estado?: string): string {
    switch (estado) {
      case 'CONFIRMADA':
        return 'text-info';
      case 'ATENDIDA':
        return 'text-success';
      case 'CANCELADA':
        return 'text-danger';
      default:
        return 'text-warning';
    }
  }
}
