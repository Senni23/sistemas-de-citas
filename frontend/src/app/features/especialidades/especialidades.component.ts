import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Especialidad } from '../../core/models/especialidad.model';
import { EspecialidadService } from '../../core/services/especialidad.service';

@Component({
  selector: 'app-especialidades',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './especialidades.component.html',
})
export class EspecialidadesComponent implements OnInit {
  especialidades: Especialidad[] = [];
  formulario: Especialidad = this.formularioVacio();
  editando = false;

  constructor(private especialidadService: EspecialidadService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.especialidadService.listar().subscribe((data) => (this.especialidades = data));
  }

  guardar(): void {
    if (this.editando && this.formulario.id) {
      this.especialidadService.actualizar(this.formulario.id, this.formulario).subscribe(() => {
        this.cargar();
        this.cancelar();
      });
    } else {
      this.especialidadService.crear(this.formulario).subscribe(() => {
        this.cargar();
        this.cancelar();
      });
    }
  }

  editar(especialidad: Especialidad): void {
    this.formulario = { ...especialidad };
    this.editando = true;
  }

  eliminar(id: number): void {
    if (confirm('Desea eliminar esta especialidad?')) {
      this.especialidadService.eliminar(id).subscribe(() => this.cargar());
    }
  }

  cancelar(): void {
    this.formulario = this.formularioVacio();
    this.editando = false;
  }

  private formularioVacio(): Especialidad {
    return { nombre: '', descripcion: '' };
  }
}
