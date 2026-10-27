import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AlertaServicio } from '../../../compartido/servicios/alerta-servicio';
import { PagoQr, PagoQrSolicitud } from '../pago-qr-modelo';
import { PagoQrServicio } from '../pago-qr-servicio';

@Component({
    selector: 'app-pago-qr-generar',
    standalone: true,
    imports: [ReactiveFormsModule],
    templateUrl: './pago-qr-generar-componente.html',
    styleUrl: './pago-qr-generar-componente.css'
})
export class PagoQrGenerarComponente {
    private formularioBuilder = inject(NonNullableFormBuilder);
    private pagoQrServicio = inject(PagoQrServicio);
    private alertaServicio = inject(AlertaServicio);

    @Output() cerrar = new EventEmitter<void>();
    @Output() guardado = new EventEmitter<PagoQr>();

    fechaMinima = new Date().toISOString().slice(0, 10);
    generando = signal<boolean>(false);

    formulario = this.formularioBuilder.group({
        amount: [0, [Validators.required, Validators.min(0.01), Validators.max(999999999999999.99)]],
        currency: ['BOB', [Validators.required, Validators.pattern(/^(BOB|USD)$/)]],
        description: ['', [Validators.maxLength(500)]],
        dueDate: [this.fechaMinima, [Validators.required]],
        singleUse: [true, [Validators.required]],
        modifyAmount: [false, [Validators.required]],
        branchCode: ['', [Validators.maxLength(5)]]
    });

    guardar(): void {
        if (this.formulario.invalid) {
            this.formulario.markAllAsTouched();
            this.alertaServicio.advertencia('Complete los campos obligatorios antes de generar el QR.');
            return;
        }
        const valores = this.formulario.getRawValue();
        const solicitud: PagoQrSolicitud = {
            amount: valores.amount,
            currency: valores.currency,
            dueDate: valores.dueDate,
            singleUse: valores.singleUse,
            modifyAmount: valores.modifyAmount
        };
        if (valores.description.trim() !== '') {
            solicitud.description = valores.description.trim();
        }
        if (valores.branchCode.trim() !== '') {
            solicitud.branchCode = valores.branchCode.trim();
        }
        this.generando.set(true);
        this.pagoQrServicio.generar(solicitud).subscribe({
            next: (pagoQr) => {
                this.generando.set(false);
                this.alertaServicio.exito('QR generado correctamente.');
                this.guardado.emit(pagoQr);
            },
            error: (err) => {
                this.generando.set(false);
                this.alertaServicio.error(this.mensajeError(err, 'No fue posible generar el QR.'));
            }
        });
    }

    cancelar(): void {
        if (!this.generando()) {
            this.cerrar.emit();
        }
    }

    private mensajeError(err: unknown, predeterminado: string): string {
        const respuesta = err as { error?: { mensaje?: string; message?: string } };
        return respuesta?.error?.mensaje || respuesta?.error?.message || predeterminado;
    }
}
