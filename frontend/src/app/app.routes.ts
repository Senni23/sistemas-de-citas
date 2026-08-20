import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { PacientesComponent } from './features/pacientes/pacientes.component';
import { MedicosComponent } from './features/medicos/medicos.component';
import { EspecialidadesComponent } from './features/especialidades/especialidades.component';
import { CitasComponent } from './features/citas/citas.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'pacientes', component: PacientesComponent, canActivate: [authGuard] },
  { path: 'medicos', component: MedicosComponent, canActivate: [authGuard] },
  { path: 'especialidades', component: EspecialidadesComponent, canActivate: [authGuard] },
  { path: 'citas', component: CitasComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' },
];
