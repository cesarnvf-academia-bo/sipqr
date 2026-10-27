import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
    providedIn: 'root'
})
export class AlertaServicio {
    exito(mensaje: string): void {
        Swal.fire({
            icon: 'success',
            title: 'Correcto',
            text: mensaje,
            timer: 2000,
            showConfirmButton: false
        });
    }

    error(mensaje: string): void {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: mensaje
        });
    }

    advertencia(mensaje: string): void {
        Swal.fire({
            icon: 'warning',
            title: 'Advertencia',
            text: mensaje
        });
    }

    informacion(mensaje: string): void {
        Swal.fire({
            icon: 'info',
            title: 'Información',
            text: mensaje
        });
    }

    async confirmar(titulo: string, mensaje: string): Promise<boolean> {
        return this.confirmarAccion(titulo, mensaje, 'Sí, eliminar', '#dc2626');
    }

    async confirmarAccion(titulo: string, mensaje: string, textoConfirmar: string,
                          colorConfirmar: string): Promise<boolean> {
        const resultado = await Swal.fire({
            icon: 'warning',
            title: titulo,
            text: mensaje,
            showCancelButton: true,
            confirmButtonText: textoConfirmar,
            cancelButtonText: 'Cancelar',
            confirmButtonColor: colorConfirmar
        });
        return resultado.isConfirmed;
    }
}
