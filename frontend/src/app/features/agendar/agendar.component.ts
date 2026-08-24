import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Especialidad } from '../../core/models/especialidad.model';
import { Medico } from '../../core/models/medico.model';
import { Servicio } from '../../core/models/servicio.model';
import { Cita } from '../../core/models/cita.model';
import { EspecialidadService } from '../../core/services/especialidad.service';
import { MedicoService } from '../../core/services/medico.service';
import { ServicioService } from '../../core/services/servicio.service';
import { CitaService } from '../../core/services/cita.service';
import { AuthService } from '../../core/services/auth.service';

const ICONOS_ESPECIALIDAD: Record<string, string> = {
  'Medicina General': 'bi-clipboard2-pulse',
  Pediatria: 'bi-emoji-smile',
  Odontologia: 'bi-emoji-laughing',
  Ginecologia: 'bi-gender-female',
  Cardiologia: 'bi-heart-pulse',
  Dermatologia: 'bi-droplet',
  Laboratorio: 'bi-eyedropper',
  'Enfermeria y Vacunacion': 'bi-bandaid',
  'Rayos X': 'bi-file-medical',
};

function generarSlots(): string[] {
  const slots: string[] = [];
  for (let h = 8; h < 14; h++) {
    slots.push(`${String(h).padStart(2, '0')}:00`);
    slots.push(`${String(h).padStart(2, '0')}:30`);
  }
  for (let h = 16; h < 20; h++) {
    slots.push(`${String(h).padStart(2, '0')}:00`);
    slots.push(`${String(h).padStart(2, '0')}:30`);
  }
  return slots;
}

@Component({
  selector: 'app-agendar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agendar.component.html',
})
export class AgendarComponent implements OnInit {
  paso = 1;

  especialidades: Especialidad[] = [];
  servicios: Servicio[] = [];
  medicos: Medico[] = [];

  serviciosDeEspecialidad: Servicio[] = [];
  medicosFiltrados: Medico[] = [];
  slots = generarSlots();
  horasOcupadas: string[] = [];

  especialidadSeleccionada: Especialidad | null = null;
  servicioSeleccionado: Servicio | null = null;
  medicoSeleccionado: Medico | null = null;
  fecha = '';
  hora = '';
  motivo = '';

  pago = { nombreTarjeta: '', numeroTarjeta: '', vencimiento: '', cvv: '' };

  fechaMinima = new Date().toISOString().split('T')[0];
  enviando = false;
  error = '';
  exito = false;

  constructor(
    private especialidadService: EspecialidadService,
    private medicoService: MedicoService,
    private servicioService: ServicioService,
    private citaService: CitaService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.especialidadService.listar().subscribe((data) => (this.especialidades = data));
    this.medicoService.listar().subscribe((data) => (this.medicos = data));
    this.servicioService.listar().subscribe((data) => (this.servicios = data));
  }

  icono(especialidad: Especialidad): string {
    return ICONOS_ESPECIALIDAD[especialidad.nombre] ?? 'bi-hospital';
  }

  elegirEspecialidad(especialidad: Especialidad): void {
    this.especialidadSeleccionada = especialidad;
    this.serviciosDeEspecialidad = this.servicios.filter((s) => s.especialidad?.id === especialidad.id);
    this.medicosFiltrados = this.medicos.filter((m) => m.especialidad?.id === especialidad.id);

    if (this.serviciosDeEspecialidad.length === 1) {
      this.servicioSeleccionado = this.serviciosDeEspecialidad[0];
      this.paso = 3;
    } else {
      this.paso = 2;
    }
  }

  elegirServicio(servicio: Servicio): void {
    this.servicioSeleccionado = servicio;
    this.paso = 3;
  }

  elegirMedico(medico: Medico): void {
    this.medicoSeleccionado = medico;
    this.paso = 4;
    if (medico.id) {
      this.citaService.listarPorMedico(medico.id).subscribe((citas) => {
        this.horasOcupadasDesde(citas);
      });
    }
  }

  onCambioFecha(): void {
    this.hora = '';
    if (this.medicoSeleccionado?.id) {
      this.citaService.listarPorMedico(this.medicoSeleccionado.id).subscribe((citas) => {
        this.horasOcupadasDesde(citas);
      });
    }
  }

  private horasOcupadasDesde(citas: Cita[]): void {
    this.horasOcupadas = citas
      .filter((c) => c.fecha === this.fecha && c.estado !== 'CANCELADA')
      .map((c) => c.hora?.substring(0, 5));
  }

  elegirHora(slot: string): void {
    if (this.horasOcupadas.includes(slot)) {
      return;
    }
    this.hora = slot;
    this.paso = 5;
  }

  volver(): void {
    if (this.paso === 3 && this.serviciosDeEspecialidad.length === 1) {
      this.paso = 1;
      return;
    }
    if (this.paso > 1) {
      this.paso -= 1;
      this.error = '';
    }
  }

  confirmarPago(): void {
    this.error = '';
    if (!this.pago.nombreTarjeta || !this.pago.numeroTarjeta || !this.pago.vencimiento || !this.pago.cvv) {
      this.error = 'Completa los datos de la tarjeta (de prueba) para continuar';
      return;
    }

    const entidadId = this.authService.usuarioActual()?.entidadId;
    const cita: Cita = {
      paciente: { id: entidadId ?? undefined } as any,
      medico: { id: this.medicoSeleccionado!.id } as Medico,
      servicio: { id: this.servicioSeleccionado!.id } as Servicio,
      fecha: this.fecha,
      hora: this.hora,
      motivo: this.motivo,
    };

    this.enviando = true;
    this.citaService.crear(cita).subscribe({
      next: (creada) => {
        this.citaService.pagar(creada.id!, 'TARJETA_SIMULADA').subscribe({
          next: () => {
            this.enviando = false;
            this.exito = true;
          },
          error: (err) => {
            this.enviando = false;
            this.error = err.error?.mensaje ?? 'La cita se creo pero el pago no se pudo procesar, intenta de nuevo desde Mis citas';
          },
        });
      },
      error: (err) => {
        this.enviando = false;
        this.error = err.error?.mensaje ?? 'No se pudo agendar la cita, intenta de nuevo';
      },
    });
  }

  irAMisCitas(): void {
    this.router.navigate(['/mis-citas']);
  }

  reiniciar(): void {
    this.paso = 1;
    this.especialidadSeleccionada = null;
    this.servicioSeleccionado = null;
    this.medicoSeleccionado = null;
    this.fecha = '';
    this.hora = '';
    this.motivo = '';
    this.pago = { nombreTarjeta: '', numeroTarjeta: '', vencimiento: '', cvv: '' };
    this.exito = false;
  }
}
