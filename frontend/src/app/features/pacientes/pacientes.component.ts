import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Paciente } from '../../core/models/paciente.model';
import { PacienteService } from '../../core/services/paciente.service';

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pacientes.component.html',
})
export class PacientesComponent implements OnInit {
  pacientes: Paciente[] = [];
  formulario: Paciente = this.formularioVacio();
  editando = false;

  constructor(private pacienteService: PacienteService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.pacienteService.listar().subscribe((data) => (this.pacientes = data));
  }

  guardar(): void {
    if (this.editando && this.formulario.id) {
      this.pacienteService.actualizar(this.formulario.id, this.formulario).subscribe(() => {
        this.cargar();
        this.cancelar();
      });
    } else {
      this.pacienteService.crear(this.formulario).subscribe(() => {
        this.cargar();
        this.cancelar();
      });
    }
  }

  editar(paciente: Paciente): void {
    this.formulario = { ...paciente };
    this.editando = true;
  }

  eliminar(id: number): void {
    if (confirm('Desea eliminar este paciente?')) {
      this.pacienteService.eliminar(id).subscribe(() => this.cargar());
    }
  }

  cancelar(): void {
    this.formulario = this.formularioVacio();
    this.editando = false;
  }

  private formularioVacio(): Paciente {
    return { nombres: '', apellidos: '', dni: '', telefono: '', email: '', direccion: '' };
  }
}
