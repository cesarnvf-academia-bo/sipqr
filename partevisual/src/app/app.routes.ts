import { Routes } from '@angular/router';
import { UsuarioListaComponente } from './componentes/usuarios/usuario-lista/usuario-lista-componente';
import { autenticacionGuardia } from './nucleo/guardias/autenticacion-guardia';
import { administradorGuardia } from './nucleo/guardias/administrador-guardia';

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
        component: UsuarioListaComponente,
        canActivate: [autenticacionGuardia, administradorGuardia]
    },
    {
        path: 'configuracionqr',
        loadComponent: () => import('./componentes/configuracion-qr/configuracion-qr-componente').then(m => m.ConfiguracionQrComponente),
        canActivate: [autenticacionGuardia, administradorGuardia]
    },
    {
        path: 'pagosqr',
        loadComponent: () => import('./componentes/pagos-qr/pago-qr-lista/pago-qr-lista-componente').then(m => m.PagoQrListaComponente),
        canActivate: [autenticacionGuardia, administradorGuardia]
    },
    {
        path: '**',
        redirectTo: ''
    }
];
