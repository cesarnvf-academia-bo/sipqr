import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PaginaRespuesta, Usuario, UsuarioActualizacion, UsuarioSolicitud } from './usuario-modelo';

@Injectable({
    providedIn: 'root'
})
export class UsuarioServicio {
    private httpCliente = inject(HttpClient);
    private urlBase = 'http://localhost:8080/api/usuarios';

    encontrarTodos(pagina: number, limite: number, buscar?: string): Observable<PaginaRespuesta<Usuario>> {
        let parametros = new HttpParams()
            .set('pagina', pagina.toString())
            .set('limite', limite.toString());
        if (buscar && buscar.trim() !== '') {
            parametros = parametros.set('buscar', buscar.trim());
        }
        return this.httpCliente.get<PaginaRespuesta<Usuario>>(this.urlBase, { params: parametros });
    }

    encontrarPorId(id: number): Observable<Usuario> {
        return this.httpCliente.get<Usuario>(`${this.urlBase}/${id}`);
    }

    adicionar(solicitud: UsuarioSolicitud): Observable<Usuario> {
        return this.httpCliente.post<Usuario>(this.urlBase, solicitud);
    }

    actualizar(id: number, actualizacion: UsuarioActualizacion): Observable<Usuario> {
        return this.httpCliente.put<Usuario>(`${this.urlBase}/${id}`, actualizacion);
    }

    borrar(id: number): Observable<void> {
        return this.httpCliente.delete<void>(`${this.urlBase}/${id}`);
    }
}
