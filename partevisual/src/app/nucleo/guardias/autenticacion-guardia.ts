import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const autenticacionGuardia: CanActivateFn = () => {
    const router = inject(Router);
    if (sessionStorage.getItem('sipqr_token')) {
        return true;
    }
    return router.createUrlTree(['/acceso']);
};
