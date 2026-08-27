import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

interface Testimonio {
  nombre: string;
  texto: string;
  iniciales: string;
}

interface Instalacion {
  nombre: string;
  descripcion: string;
  icono: string;
}

interface EspecialidadResumen {
  nombre: string;
  icono: string;
}

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.css',
})
export class InicioComponent implements OnInit {
  testimonios: Testimonio[] = [
    {
      nombre: 'Maria Fernanda Garcia Torres',
      iniciales: 'MG',
      texto: 'Desde que empece a atenderme en Vitalis, agendar una cita es cuestion de minutos desde el celular. El personal es muy amable y puntual.',
    },
    {
      nombre: 'Carlos Alberto Ramirez Quispe',
      iniciales: 'CR',
      texto: 'Lleve a mi hijo por una consulta de pediatria y la atencion fue rapidisima, sin las esperas de siempre. Totalmente recomendado.',
    },
    {
      nombre: 'Roberto Carlos Flores Chavez',
      iniciales: 'RF',
      texto: 'Me hice mis analisis en el laboratorio de la clinica y tuve mis resultados en el tiempo exacto que me indicaron. Muy profesionales.',
    },
  ];

  instalaciones: Instalacion[] = [
    { nombre: 'Sala de Rayos X', descripcion: 'Equipo de imagenologia para diagnosticos precisos', icono: 'bi-file-medical' },
    { nombre: 'Laboratorio Clinico', descripcion: 'Analisis de sangre, orina y heces con resultados rapidos', icono: 'bi-eyedropper' },
    { nombre: 'Consultorios especializados', descripcion: 'Espacios equipados para cada especialidad medica', icono: 'bi-hospital' },
    { nombre: 'Area de Enfermeria y Vacunacion', descripcion: 'Inyectables, curaciones y controles rutinarios', icono: 'bi-bandaid' },
    { nombre: 'Sala de espera', descripcion: 'Ambientes comodos pensados para tu tranquilidad', icono: 'bi-people' },
    { nombre: 'Recepcion y admision', descripcion: 'Registro rapido para que tu cita empiece a tiempo', icono: 'bi-building' },
  ];

  especialidades: EspecialidadResumen[] = [
    { nombre: 'Medicina General', icono: 'bi-clipboard2-pulse' },
    { nombre: 'Pediatria', icono: 'bi-emoji-smile' },
    { nombre: 'Odontologia', icono: 'bi-emoji-laughing' },
    { nombre: 'Ginecologia', icono: 'bi-gender-female' },
    { nombre: 'Cardiologia', icono: 'bi-heart-pulse' },
    { nombre: 'Dermatologia', icono: 'bi-droplet' },
    { nombre: 'Laboratorio', icono: 'bi-eyedropper' },
    { nombre: 'Enfermeria y Vacunacion', icono: 'bi-bandaid' },
    { nombre: 'Rayos X', icono: 'bi-file-medical' },
  ];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (this.authService.estaAutenticado()) {
      this.router.navigate([this.authService.rutaInicioSegunRol()]);
    }
  }
}
