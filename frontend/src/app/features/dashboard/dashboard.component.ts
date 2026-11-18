import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService, ResumenDashboard } from '../../core/services/dashboard.service';
import { EstadoCita } from '../../core/models/cita.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  resumen: ResumenDashboard | null = null;

  constructor(private dashboardService: DashboardService) {}

  /** Color de la etiqueta de cada estado (mismas clases que usa la lista de citas). */
  claseDelEstado(estado: EstadoCita): string {
    switch (estado) {
      case 'PENDIENTE':
        return 'text-warning';
      case 'CONFIRMADA':
        return 'text-info';
      case 'ATENDIDA':
        return 'text-success';
      default:
        return 'text-danger';
    }
  }

  ngOnInit(): void {
    this.dashboardService.resumen().subscribe((r) => (this.resumen = r));
  }
}
