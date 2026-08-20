import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cita, EstadoCita } from '../../core/models/cita.model';
import { Paciente } from '../../core/models/paciente.model';
import { Medico } from '../../core/models/medico.model';
import { CitaService } from '../../core/services/cita.service';
import { PacienteService } from '../../core/services/paciente.service';
import { MedicoService } from '../../core/services/medico.service';

@Component({
  selector: 'app-citas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './citas.component.html',
})
export class CitasComponent implements OnInit {
  citas: Cita[] = [];
  pacientes: Paciente[] = [];
  medicos: Medico[] = [];
  estados: EstadoCita[] = ['PENDIENTE', 'CONFIRMADA', 'ATENDIDA', 'CANCELADA'];

  formulario: { pacienteId?: number; medicoId?: number; fecha: string; hora: string; motivo: string } =
    this.formularioVacio();

  constructor(
    private citaService: CitaService,
    private pacienteService: PacienteService,
    private medicoService: MedicoService
  ) {}

  ngOnInit(): void {
    this.cargar();
    this.pacienteService.listar().subscribe((data) => (this.pacientes = data));
    this.medicoService.listar().subscribe((data) => (this.medicos = data));
  }

  cargar(): void {
    this.citaService.listar().subscribe((data) => (this.citas = data));
  }

  agendar(): void {
    const cita: Cita = {
      paciente: { id: Number(this.formulario.pacienteId) } as Paciente,
      medico: { id: Number(this.formulario.medicoId) } as Medico,
      fecha: this.formulario.fecha,
      hora: this.formulario.hora,
      motivo: this.formulario.motivo,
    };
    this.citaService.crear(cita).subscribe(() => {
      this.cargar();
      this.formulario = this.formularioVacio();
    });
  }

  cambiarEstado(cita: Cita, estado: EstadoCita): void {
    this.citaService.cambiarEstado(cita.id!, estado).subscribe(() => this.cargar());
  }

  eliminar(id: number): void {
    if (confirm('Desea eliminar esta cita?')) {
      this.citaService.eliminar(id).subscribe(() => this.cargar());
    }
  }

  private formularioVacio() {
    return { pacienteId: undefined, medicoId: undefined, fecha: '', hora: '', motivo: '' };
  }
}
