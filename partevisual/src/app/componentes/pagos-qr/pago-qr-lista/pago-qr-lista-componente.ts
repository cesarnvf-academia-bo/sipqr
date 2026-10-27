import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AlertaServicio } from '../../../compartido/servicios/alerta-servicio';
import { AutenticacionServicio } from '../../../nucleo/autenticacion/autenticacion-servicio';
import { PagoQr } from '../pago-qr-modelo';
import { PagoQrServicio } from '../pago-qr-servicio';
import { PagoQrDetalleComponente } from '../pago-qr-detalle/pago-qr-detalle-componente';
import { PagoQrGenerarComponente } from '../pago-qr-generar/pago-qr-generar-componente';

@Component({
    selector: 'app-pago-qr-lista',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, PagoQrGenerarComponente, PagoQrDetalleComponente],
    templateUrl: './pago-qr-lista-componente.html',
    styleUrl: './pago-qr-lista-componente.css'
})
export class PagoQrListaComponente implements OnInit {
    private pagoQrServicio = inject(PagoQrServicio);
    private alertaServicio = inject(AlertaServicio);
    private autenticacionServicio = inject(AutenticacionServicio);
    private router = inject(Router);

    pagos = signal<PagoQr[]>([]);
    pagina = signal<number>(1);
    limite = signal<number>(5);
    buscar = signal<string>('');
    textoBuscar = signal<string>('');
    estadoFiltro = signal<string>('');
    totalRegistros = signal<number>(0);
    totalPaginas = signal<number>(0);
    cargando = signal<boolean>(false);
    mostrarGenerar = signal<boolean>(false);
    pagoDetalleId = signal<number | null>(null);

    ngOnInit(): void {
        this.cargarPagos();
    }

    cargarPagos(): void {
        this.cargando.set(true);
        this.pagoQrServicio.encontrarTodos(this.pagina(), this.limite(), this.buscar(), this.estadoFiltro()).subscribe({
            next: (respuesta) => {
                this.pagos.set(respuesta.datos);
                this.totalRegistros.set(respuesta.totalRegistros);
                this.totalPaginas.set(respuesta.totalPaginas);
                this.pagina.set(respuesta.pagina);
                this.limite.set(respuesta.limite);
                this.cargando.set(false);
            },
            error: () => {
                this.cargando.set(false);
                this.alertaServicio.error('No fue posible cargar los pagos QR.');
            }
        });
    }

    buscarPagos(): void {
        this.buscar.set(this.textoBuscar());
        this.pagina.set(1);
        this.cargarPagos();
    }

    limpiarFiltros(): void {
        this.textoBuscar.set('');
        this.buscar.set('');
        this.estadoFiltro.set('');
        this.pagina.set(1);
        this.cargarPagos();
    }

    cambiarLimite(evento: Event): void {
        const valor = parseInt((evento.target as HTMLSelectElement).value, 10);
        this.limite.set(valor);
        this.pagina.set(1);
        this.cargarPagos();
    }

    cambiarPagina(nuevaPagina: number): void {
        if (nuevaPagina < 1 || nuevaPagina > this.totalPaginas()) {
            return;
        }
        this.pagina.set(nuevaPagina);
        this.cargarPagos();
    }

    abrirGenerar(): void {
        this.mostrarGenerar.set(true);
    }

    cerrarGenerar(): void {
        this.mostrarGenerar.set(false);
    }

    alGenerar(pagoQr: PagoQr): void {
        this.mostrarGenerar.set(false);
        this.cargarPagos();
        this.pagoDetalleId.set(pagoQr.id);
    }

    abrirDetalle(pagoQr: PagoQr): void {
        this.pagoDetalleId.set(pagoQr.id);
    }

    cerrarDetalle(): void {
        this.pagoDetalleId.set(null);
    }

    alActualizarDetalle(): void {
        this.cargarPagos();
    }

    async verificarDesdeLista(pagoQr: PagoQr): Promise<void> {
        this.pagoQrServicio.verificar(pagoQr.id).subscribe({
            next: (actualizado) => {
                if (actualizado.estado === 'PAGADO') {
                    this.alertaServicio.exito('Pago confirmado por BANECO.');
                } else {
                    this.alertaServicio.informacion(`Estado actual: ${actualizado.estado}.`);
                }
                this.cargarPagos();
            },
            error: (err) => {
                this.alertaServicio.error(this.mensajeError(err, 'No fue posible verificar el QR.'));
            }
        });
    }

    async anularDesdeLista(pagoQr: PagoQr): Promise<void> {
        const confirmado = await this.alertaServicio.confirmarAccion(
            '¿Anular QR?',
            'El QR dejará de aceptar pagos. Esta acción se enviará a BANECO.',
            'Sí, anular',
            '#dc2626'
        );
        if (!confirmado) {
            return;
        }
        this.pagoQrServicio.anular(pagoQr.id).subscribe({
            next: () => {
                this.alertaServicio.exito('QR anulado correctamente.');
                this.cargarPagos();
            },
            error: (err) => {
                this.alertaServicio.error(this.mensajeError(err, 'No fue posible anular el QR.'));
            }
        });
    }

    async borrarPago(pagoQr: PagoQr): Promise<void> {
        const confirmado = await this.alertaServicio.confirmar(
            '¿Eliminar pago QR?',
            'Solo se eliminan registros que aún no tienen QR generado. Esta acción no se puede deshacer.'
        );
        if (!confirmado) {
            return;
        }
        this.pagoQrServicio.borrar(pagoQr.id).subscribe({
            next: () => {
                this.alertaServicio.exito('Pago QR eliminado correctamente.');
                if (this.pagos().length === 1 && this.pagina() > 1) {
                    this.pagina.set(this.pagina() - 1);
                }
                this.cargarPagos();
            },
            error: (err) => {
                this.alertaServicio.error(this.mensajeError(err, 'No fue posible eliminar el pago QR.'));
            }
        });
    }

    cerrarSesion(): void {
        this.autenticacionServicio.cerrarSesion();
        void this.router.navigate(['/acceso']);
    }

    estadoClase(estado: string): string {
        switch (estado) {
            case 'PAGADO':
                return 'bg-green-100 text-green-700';
            case 'PENDIENTE':
                return 'bg-amber-100 text-amber-700';
            case 'ANULADO':
                return 'bg-slate-200 text-slate-700';
            case 'VENCIDO':
                return 'bg-orange-100 text-orange-700';
            case 'ERROR':
                return 'bg-red-100 text-red-700';
            case 'GENERANDO':
                return 'bg-blue-100 text-blue-700';
            default:
                return 'bg-slate-100 text-slate-700';
        }
    }

    get paginasArray(): number[] {
        const total = this.totalPaginas();
        if (total <= 0) {
            return [];
        }
        const paginas: number[] = [];
        for (let indice = 1; indice <= total; indice++) {
            paginas.push(indice);
        }
        return paginas;
    }

    private mensajeError(err: unknown, predeterminado: string): string {
        const respuesta = err as { error?: { mensaje?: string; message?: string } };
        return respuesta?.error?.mensaje || respuesta?.error?.message || predeterminado;
    }
}
