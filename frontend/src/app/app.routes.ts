import { Routes } from '@angular/router';
import { adminGuard, medicoGuard, pacienteGuard } from './core/guards/auth.guard';
import { InicioComponent } from './features/inicio/inicio.component';
import { LoginComponent } from './features/login/login.component';
import { RegistroComponent } from './features/registro/registro.component';
import { OlvidePasswordComponent } from './features/olvide-password/olvide-password.component';
import { RestablecerPasswordComponent } from './features/restablecer-password/restablecer-password.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { PacientesComponent } from './features/pacientes/pacientes.component';
import { MedicosComponent } from './features/medicos/medicos.component';
import { EspecialidadesComponent } from './features/especialidades/especialidades.component';
import { CitasComponent } from './features/citas/citas.component';
import { FarmaciaComponent } from './features/farmacia/farmacia.component';
import { AgendarComponent } from './features/agendar/agendar.component';
import { MisCitasComponent } from './features/mis-citas/mis-citas.component';
import { MiAgendaComponent } from './features/mi-agenda/mi-agenda.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'olvide-password', component: OlvidePasswordComponent },
  { path: 'restablecer-password', component: RestablecerPasswordComponent },

  // Panel de administracion (personal de la clinica)
  { path: 'dashboard', component: DashboardComponent, canActivate: [adminGuard] },
  { path: 'pacientes', component: PacientesComponent, canActivate: [adminGuard] },
  { path: 'medicos', component: MedicosComponent, canActivate: [adminGuard] },
  { path: 'especialidades', component: EspecialidadesComponent, canActivate: [adminGuard] },
  { path: 'citas', component: CitasComponent, canActivate: [adminGuard] },
  { path: 'farmacia', component: FarmaciaComponent, canActivate: [adminGuard] },

  // Portal del medico
  { path: 'mi-agenda', component: MiAgendaComponent, canActivate: [medicoGuard] },

  // Portal del paciente
  { path: 'agendar', component: AgendarComponent, canActivate: [pacienteGuard] },
  { path: 'mis-citas', component: MisCitasComponent, canActivate: [pacienteGuard] },

  { path: '', component: InicioComponent },
  { path: '**', redirectTo: '' },
];
