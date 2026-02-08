import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';

/**
 * Rutas principales de la aplicación
 * Por ahora solo tenemos el dashboard como ruta principal
 */
export const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    data: { title: 'Panel de Incidencias Urbanas' }
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    data: { title: 'Dashboard' }
  },
  {
    path: '**',
    redirectTo: ''
  }
];
