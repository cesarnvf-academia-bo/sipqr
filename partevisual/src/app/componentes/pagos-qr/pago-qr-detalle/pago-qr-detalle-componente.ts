import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, computed, inject, signal } from '@angular/core';
import { AlertaServicio } from '../../../compartido/servicios/alerta-servicio';
import { PagoQr } from '../pago-qr-modelo';
import { PagoQrServicio } from '../pago-qr-servicio';

@Component({
    selector: 'app-pago-qr-detalle',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './pago-qr-detalle-componente.html',
    styleUrl: './pago-qr-detalle-componente.css'
})
export class PagoQrDetalleComponente implements OnInit, OnDestroy {
    private pagoQrServicio = inject(PagoQrServicio);
    private alertaServicio = inject(AlertaServicio);
    private temporizador: ReturnType<typeof setTimeout> | null = null;
    private intervaloMs = 5000;
    private destruido = false;
    private pagoNotificado = false;

    @Input({ required: true }) pagoId!: number;
    @Output() cerrar = new EventEmitter<void>();
    @Output() actualizado = new EventEmitter<PagoQr>();
    @Output() anulado = new EventEmitter<PagoQr>();

    pago = signal<PagoQr | null>(null);
    cargando = signal<boolean>(true);
    errorCarga = signal<string>('');
    verificando = signal<boolean>(false);
    anulando = signal<boolean>(false);
    errorSondeo = signal<string>('');
    qrImagenUrl = computed<string | null>(() => {
        const actual = this.pago();
        return actual?.qrImage ? `data:image/png;base64,${actual.qrImage}` : null;
    });

    ngOnInit(): void {
        this.cargar();
    }

    ngOnDestroy(): void {
        this.destruido = true;
        this.detenerSondeo();
    }

    cargar(): void {
        this.cargando.set(true);
        this.errorCarga.set('');
        this.pagoQrServicio.encontrarPorId(this.pagoId).subscribe({
            next: (pagoQr) => {
                this.cargando.set(false);
                this.pago.set(pagoQr);
                if (pagoQr.estado === 'PENDIENTE') {
                    this.intervaloMs = 5000;
                    this.programarSondeo();
                }
            },
            error: () => {
                this.cargando.set(false);
                this.errorCarga.set('No fue posible cargar el pago QR.');
            }
        });
    }

    verificarManual(): void {
        this.ejecutarVerificacion(true);
    }

    async anular(): Promise<void> {
        const actual = this.pago();
        if (!actual) {
            return;
        }
        const confirmado = await this.alertaServicio.confirmarAccion(
            '¿Anular QR?',
            'El QR dejará de aceptar pagos. Esta acción se enviará a BANECO.',
            'Sí, anular',
            '#dc2626'
        );
        if (!confirmado) {
            return;
        }
        this.anulando.set(true);
        this.detenerSondeo();
        this.pagoQrServicio.anular(actual.id).subscribe({
            next: (pagoQr) => {
                this.anulando.set(false);
                this.pago.set(pagoQr);
                this.alertaServicio.exito('QR anulado correctamente.');
                this.actualizado.emit(pagoQr);
                this.anulado.emit(pagoQr);
            },
            error: (err) => {
                this.anulando.set(false);
                this.alertaServicio.error(this.mensajeError(err, 'No fue posible anular el QR.'));
                if (this.pago()?.estado === 'PENDIENTE') {
                    this.programarSondeo();
                }
            }
        });
    }

    cerrarModal(): void {
        this.detenerSondeo();
        this.cerrar.emit();
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

    private ejecutarVerificacion(manual: boolean): void {
        const actual = this.pago();
        if (!actual || this.verificando()) {
            return;
        }
        this.detenerSondeo();
        this.verificando.set(true);
        this.pagoQrServicio.verificar(actual.id).subscribe({
            next: (pagoQr) => {
                this.verificando.set(false);
                this.errorSondeo.set('');
                this.intervaloMs = 5000;
                this.pago.set(pagoQr);
                this.actualizado.emit(pagoQr);
                this.notificarEstado(pagoQr);
                if (pagoQr.estado === 'PENDIENTE') {
                    this.programarSondeo();
                }
            },
            error: (err) => {
                this.verificando.set(false);
                this.errorSondeo.set('No fue posible consultar el estado del QR. Se reintentará.');
                if (manual) {
                    this.alertaServicio.error(this.mensajeError(err, 'No fue posible verificar el QR.'));
                }
                if (this.pago()?.estado === 'PENDIENTE') {
                    this.intervaloMs = Math.min(this.intervaloMs * 2, 30000);
                    this.programarSondeo();
                }
            }
        });
    }

    private programarSondeo(): void {
        this.detenerSondeo();
        if (this.destruido || this.pago()?.estado !== 'PENDIENTE') {
            return;
        }
        this.temporizador = setTimeout(() => this.ejecutarVerificacion(false), this.intervaloMs);
    }

    private detenerSondeo(): void {
        if (this.temporizador) {
            clearTimeout(this.temporizador);
            this.temporizador = null;
        }
    }

    private notificarEstado(pagoQr: PagoQr): void {
        if (pagoQr.estado === 'PAGADO') {
            if (!this.pagoNotificado) {
                this.pagoNotificado = true;
                this.alertaServicio.exito('Pago confirmado por BANECO.');
            }
            return;
        }
        if (pagoQr.estado === 'ANULADO' && !this.pagoNotificado) {
            this.pagoNotificado = true;
            this.alertaServicio.informacion('El QR fue anulado en BANECO.');
            return;
        }
        if (pagoQr.estado === 'VENCIDO' && !this.pagoNotificado) {
            this.pagoNotificado = true;
            this.alertaServicio.advertencia('El QR venció sin completar el pago.');
        }
    }

    private mensajeError(err: unknown, predeterminado: string): string {
        const respuesta = err as { error?: { mensaje?: string; message?: string } };
        return respuesta?.error?.mensaje || respuesta?.error?.message || predeterminado;
    }
}
