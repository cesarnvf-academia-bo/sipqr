import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

interface AccesoRespuesta {
    token: string;
    tipo: string;
    usuario: string;
    rol: string;
    expiraEn: number;
}

@Injectable({
    providedIn: 'root'
})
export class AutenticacionServicio {
    private httpCliente = inject(HttpClient);
    private urlBase = 'http://localhost:8080/api/acceso';
    private claveToken = 'sipqr_token';
    private claveUsuario = 'sipqr_usuario';
    private claveRol = 'sipqr_rol';

    private tokenActual = signal<string | null>(sessionStorage.getItem(this.claveToken));
    private usuarioActual = signal<string | null>(sessionStorage.getItem(this.claveUsuario));
    private rolActual = signal<string | null>(sessionStorage.getItem(this.claveRol));

    readonly autenticado = computed(() => this.tokenActual() !== null);
    readonly usuario = computed(() => this.usuarioActual());
    readonly rol = computed(() => this.rolActual());
    readonly esAdministrador = computed(() => this.rolActual() === 'ADMIN');

    iniciarSesion(usuario: string, clave: string): Observable<AccesoRespuesta> {
        return this.httpCliente.post<AccesoRespuesta>(this.urlBase, { usuario, clave }).pipe(
            tap((respuesta) => {
                sessionStorage.setItem(this.claveToken, respuesta.token);
                sessionStorage.setItem(this.claveUsuario, respuesta.usuario);
                sessionStorage.setItem(this.claveRol, respuesta.rol);
                this.tokenActual.set(respuesta.token);
                this.usuarioActual.set(respuesta.usuario);
                this.rolActual.set(respuesta.rol);
            })
        );
    }

    cerrarSesion(): void {
        sessionStorage.removeItem(this.claveToken);
        sessionStorage.removeItem(this.claveUsuario);
        sessionStorage.removeItem(this.claveRol);
        this.tokenActual.set(null);
        this.usuarioActual.set(null);
        this.rolActual.set(null);
    }

    obtenerToken(): string | null {
        return this.tokenActual();
    }
}
