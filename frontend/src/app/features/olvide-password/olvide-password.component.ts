import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-olvide-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './olvide-password.component.html',
})
export class OlvidePasswordComponent {
  email = '';
  error = '';
  mensaje = '';
  cargando = false;

  constructor(private authService: AuthService) {}

  enviar(): void {
    this.error = '';
    this.mensaje = '';

    if (!this.email) {
      this.error = 'Ingresa tu correo';
      return;
    }

    this.cargando = true;
    this.authService.olvidePassword(this.email).subscribe({
      next: (res) => {
        this.cargando = false;
        this.mensaje = res.mensaje;
      },
      error: () => {
        this.cargando = false;
        this.error = 'No se pudo procesar la solicitud, intenta nuevamente';
      },
    });
  }
}
