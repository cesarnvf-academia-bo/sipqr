import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ConfiguracionQr, ConfiguracionQrSolicitud } from './configuracion-qr-modelo';

@Injectable({
    providedIn: 'root'
})
export class ConfiguracionQrServicio {
    private httpCliente = inject(HttpClient);
    private urlBase = 'http://localhost:8080/api/configuracionqr';

    obtener(): Observable<ConfiguracionQr> {
        return this.httpCliente.get<ConfiguracionQr>(this.urlBase);
    }

    actualizar(solicitud: ConfiguracionQrSolicitud): Observable<ConfiguracionQr> {
        return this.httpCliente.put<ConfiguracionQr>(this.urlBase, solicitud);
    }
}
