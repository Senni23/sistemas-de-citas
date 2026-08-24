import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Cita } from '../../core/models/cita.model';
import { CitaService } from '../../core/services/cita.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-mis-citas',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mis-citas.component.html',
})
export class MisCitasComponent implements OnInit {
  citas: Cita[] = [];
  cargando = true;

  constructor(private citaService: CitaService, private authService: AuthService) {}

  ngOnInit(): void {
    const pacienteId = this.authService.usuarioActual()?.entidadId;
    if (!pacienteId) {
      this.cargando = false;
      return;
    }
    this.citaService.listarPorPaciente(pacienteId).subscribe((data) => {
      this.citas = data.sort((a, b) => (a.fecha + a.hora).localeCompare(b.fecha + b.hora));
      this.cargando = false;
    });
  }

  puedeCancelar(cita: Cita): boolean {
    return cita.estado === 'PENDIENTE' || cita.estado === 'CONFIRMADA';
  }

  cancelar(cita: Cita): void {
    if (!cita.id || !confirm('¿Seguro que quieres cancelar esta cita?')) {
      return;
    }
    this.citaService.cambiarEstado(cita.id, 'CANCELADA').subscribe(() => {
      cita.estado = 'CANCELADA';
    });
  }

  completarPago(cita: Cita): void {
    if (!cita.id) {
      return;
    }
    this.citaService.pagar(cita.id, 'TARJETA_SIMULADA').subscribe((actualizada) => {
      cita.pagado = actualizada.pagado;
      cita.estado = actualizada.estado;
      cita.montoPagado = actualizada.montoPagado;
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
