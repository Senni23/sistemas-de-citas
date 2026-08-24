import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  ingresar(): void {
    this.error = '';
    this.authService.login(this.username, this.password).subscribe({
      next: () => this.router.navigate([this.authService.rutaInicioSegunRol()]),
      error: () => (this.error = 'Usuario o contrasena incorrectos'),
    });
  }
}
