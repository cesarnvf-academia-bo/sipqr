import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const administradorGuardia: CanActivateFn = () => {
    const router = inject(Router);
    if (sessionStorage.getItem('sipqr_rol') === 'ADMIN') {
        return true;
    }
    return router.createUrlTree(['/acceso']);
};
