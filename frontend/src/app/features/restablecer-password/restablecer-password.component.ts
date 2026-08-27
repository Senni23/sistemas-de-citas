import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-restablecer-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './restablecer-password.component.html',
})
export class RestablecerPasswordComponent implements OnInit {
  token = '';
  password = '';
  confirmarPassword = '';
  error = '';
  mensaje = '';
  cargando = false;
  mostrarPassword = false;
  mostrarConfirmarPassword = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
    if (!this.token) {
      this.error = 'El enlace no es valido, solicita uno nuevo';
    }
  }

  restablecer(): void {
    this.error = '';
    this.mensaje = '';

    if (this.password.length < 6) {
      this.error = 'La contrasena debe tener al menos 6 caracteres';
      return;
    }
    if (this.password !== this.confirmarPassword) {
      this.error = 'Las contrasenas no coinciden';
      return;
    }

    this.cargando = true;
    this.authService.restablecerPassword(this.token, this.password).subscribe({
      next: (res) => {
        this.cargando = false;
        this.mensaje = res.mensaje;
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: (err) => {
        this.cargando = false;
        this.error = err.error?.mensaje ?? 'No se pudo restablecer la contrasena';
      },
    });
  }
}
