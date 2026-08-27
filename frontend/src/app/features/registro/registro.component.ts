import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registro.component.html',
})
export class RegistroComponent {
  formulario = {
    nombres: '',
    apellidos: '',
    dni: '',
    telefono: '',
    email: '',
    direccion: '',
    password: '',
    confirmarPassword: '',
  };
  error = '';
  cargando = false;
  mostrarPassword = false;
  mostrarConfirmarPassword = false;

  constructor(private authService: AuthService, private router: Router) {}

  registrar(): void {
    this.error = '';

    if (!this.formulario.nombres || !this.formulario.apellidos || !this.formulario.dni || !this.formulario.email) {
      this.error = 'Completa tus nombres, apellidos, DNI y correo';
      return;
    }
    if (this.formulario.password.length < 6) {
      this.error = 'La contrasena debe tener al menos 6 caracteres';
      return;
    }
    if (this.formulario.password !== this.formulario.confirmarPassword) {
      this.error = 'Las contrasenas no coinciden';
      return;
    }

    this.cargando = true;
    this.authService.registrarPaciente(this.formulario).subscribe({
      next: () => this.router.navigate([this.authService.rutaInicioSegunRol()]),
      error: (err) => {
        this.cargando = false;
        this.error = err.error?.mensaje ?? 'No se pudo completar el registro';
      },
    });
  }
}
