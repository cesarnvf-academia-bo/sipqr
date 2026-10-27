import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (solicitud, siguiente) => {
    const router = inject(Router);
    const token = sessionStorage.getItem('sipqr_token');
    const solicitudConToken = token
        ? solicitud.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
        : solicitud;

    return siguiente(solicitudConToken).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                sessionStorage.removeItem('sipqr_token');
                sessionStorage.removeItem('sipqr_usuario');
                sessionStorage.removeItem('sipqr_rol');
                void router.navigate(['/acceso']);
            }
            return throwError(() => error);
        })
    );
};
