import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/dashboard/dashboard-page/dashboard-page').then(
        (m) => m.DashboardPage
      )
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];