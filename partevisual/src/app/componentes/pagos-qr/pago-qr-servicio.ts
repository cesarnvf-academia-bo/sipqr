import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PaginaPagoQr, PagoQr, PagoQrSolicitud } from './pago-qr-modelo';

@Injectable({
    providedIn: 'root'
})
export class PagoQrServicio {
    private httpCliente = inject(HttpClient);
    private urlBase = 'http://localhost:8080/api/pagosqr';

    encontrarTodos(pagina: number, limite: number, buscar?: string, estado?: string): Observable<PaginaPagoQr> {
        let parametros = new HttpParams()
            .set('pagina', pagina.toString())
            .set('limite', limite.toString());
        if (buscar && buscar.trim() !== '') {
            parametros = parametros.set('buscar', buscar.trim());
        }
        if (estado && estado.trim() !== '') {
            parametros = parametros.set('estado', estado.trim());
        }
        return this.httpCliente.get<PaginaPagoQr>(this.urlBase, { params: parametros });
    }

    encontrarPorId(id: number): Observable<PagoQr> {
        return this.httpCliente.get<PagoQr>(`${this.urlBase}/${id}`);
    }

    generar(solicitud: PagoQrSolicitud): Observable<PagoQr> {
        return this.httpCliente.post<PagoQr>(this.urlBase, solicitud);
    }

    verificar(id: number): Observable<PagoQr> {
        return this.httpCliente.post<PagoQr>(`${this.urlBase}/${id}/verificar`, {});
    }

    anular(id: number): Observable<PagoQr> {
        return this.httpCliente.post<PagoQr>(`${this.urlBase}/${id}/anular`, {});
    }

    borrar(id: number): Observable<void> {
        return this.httpCliente.delete<void>(`${this.urlBase}/${id}`);
    }
}
