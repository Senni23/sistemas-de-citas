import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Medico, MedicoRegistro } from '../../core/models/medico.model';
import { Especialidad } from '../../core/models/especialidad.model';
import { MedicoService } from '../../core/services/medico.service';
import { EspecialidadService } from '../../core/services/especialidad.service';

@Component({
  selector: 'app-medicos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './medicos.component.html',
})
export class MedicosComponent implements OnInit {
  medicos: Medico[] = [];
  especialidades: Especialidad[] = [];
  formulario: Partial<Medico> & { especialidadId?: number; password?: string } = this.formularioVacio();
  editando = false;

  constructor(private medicoService: MedicoService, private especialidadService: EspecialidadService) {}

  ngOnInit(): void {
    this.cargar();
    this.especialidadService.listar().subscribe((data) => (this.especialidades = data));
  }

  cargar(): void {
    this.medicoService.listar().subscribe((data) => (this.medicos = data));
  }

  error = '';

  guardar(): void {
    this.error = '';

    if (this.editando && this.formulario.id) {
      const medico: Medico = {
        id: this.formulario.id,
        nombres: this.formulario.nombres ?? '',
        apellidos: this.formulario.apellidos ?? '',
        colegiatura: this.formulario.colegiatura ?? '',
        telefono: this.formulario.telefono,
        email: this.formulario.email,
        especialidad: { id: Number(this.formulario.especialidadId) } as Especialidad,
      };
      this.medicoService.actualizar(medico.id!, medico).subscribe({
        next: () => {
          this.cargar();
          this.cancelar();
        },
        error: (err) => (this.error = err.error?.mensaje ?? 'No se pudo guardar'),
      });
    } else {
      const registro: MedicoRegistro = {
        nombres: this.formulario.nombres ?? '',
        apellidos: this.formulario.apellidos ?? '',
        colegiatura: this.formulario.colegiatura ?? '',
        telefono: this.formulario.telefono,
        email: this.formulario.email ?? '',
        especialidadId: Number(this.formulario.especialidadId),
        password: this.formulario.password ?? '',
      };
      this.medicoService.crear(registro).subscribe({
        next: () => {
          this.cargar();
          this.cancelar();
        },
        error: (err) => (this.error = err.error?.mensaje ?? 'No se pudo crear el medico'),
      });
    }
  }

  editar(medico: Medico): void {
    this.formulario = { ...medico, especialidadId: medico.especialidad?.id };
    this.editando = true;
  }

  eliminar(id: number): void {
    if (confirm('Desea eliminar este medico?')) {
      this.medicoService.eliminar(id).subscribe(() => this.cargar());
    }
  }

  cancelar(): void {
    this.formulario = this.formularioVacio();
    this.editando = false;
  }

  private formularioVacio() {
    return { nombres: '', apellidos: '', colegiatura: '', telefono: '', email: '', especialidadId: undefined, password: '' };
  }
}
