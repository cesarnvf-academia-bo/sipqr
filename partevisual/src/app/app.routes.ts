import { Routes } from '@angular/router';
import { UsuarioListaComponente } from './componentes/usuarios/usuario-lista/usuario-lista-componente';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () => import('./componentes/inicio/inicio-componente').then(m => m.InicioComponente)
    },
    {
        path: 'acceso',
        loadComponent: () => import('./componentes/acceso/acceso-componente').then(m => m.AccesoComponente)
    },
    {
        path: 'usuarios',
        component: UsuarioListaComponente
    },
    {
        path: '**',
        redirectTo: ''
    }
];
