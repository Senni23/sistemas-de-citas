import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Medico } from '../../core/models/medico.model';
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
  formulario: Partial<Medico> & { especialidadId?: number } = this.formularioVacio();
  editando = false;

  constructor(private medicoService: MedicoService, private especialidadService: EspecialidadService) {}

  ngOnInit(): void {
    this.cargar();
    this.especialidadService.listar().subscribe((data) => (this.especialidades = data));
  }

  cargar(): void {
    this.medicoService.listar().subscribe((data) => (this.medicos = data));
  }

  guardar(): void {
    const medico: Medico = {
      id: this.formulario.id,
      nombres: this.formulario.nombres ?? '',
      apellidos: this.formulario.apellidos ?? '',
      colegiatura: this.formulario.colegiatura ?? '',
      telefono: this.formulario.telefono,
      email: this.formulario.email,
      especialidad: { id: Number(this.formulario.especialidadId) } as Especialidad,
    };

    if (this.editando && medico.id) {
      this.medicoService.actualizar(medico.id, medico).subscribe(() => {
        this.cargar();
        this.cancelar();
      });
    } else {
      this.medicoService.crear(medico).subscribe(() => {
        this.cargar();
        this.cancelar();
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
    return { nombres: '', apellidos: '', colegiatura: '', telefono: '', email: '', especialidadId: undefined };
  }
}
