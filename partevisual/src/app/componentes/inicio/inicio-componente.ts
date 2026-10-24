import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-inicio',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './inicio-componente.html',
    styleUrl: './inicio-componente.css'
})
export class InicioComponente {
    private httpCliente = inject(HttpClient);
    estadoConexion = signal<string>('verificando');
    respuesta = signal<any>(null);

    constructor() {
        this.verificarConexion();
    }

    verificarConexion(): void {
        this.httpCliente.get('http://localhost:8080/api/verificacion/estado').subscribe({
            next: (datos) => {
                this.respuesta.set(datos);
                this.estadoConexion.set('conectado');
            },
            error: () => {
                this.estadoConexion.set('sinConexion');
            }
        });
    }
}
