import { Component, OnInit, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AlertaServicio } from '../../compartido/servicios/alerta-servicio';
import { ConfiguracionQr, ConfiguracionQrSolicitud } from './configuracion-qr-modelo';
import { ConfiguracionQrServicio } from './configuracion-qr-servicio';

@Component({
    selector: 'app-configuracion-qr',
    standalone: true,
    imports: [ReactiveFormsModule, RouterLink],
    templateUrl: './configuracion-qr-componente.html',
    styleUrl: './configuracion-qr-componente.css'
})
export class ConfiguracionQrComponente implements OnInit {
    private formularioBuilder = inject(NonNullableFormBuilder);
    private configuracionQrServicio = inject(ConfiguracionQrServicio);
    private alertaServicio = inject(AlertaServicio);

    formulario = this.formularioBuilder.group({
        userName: ['', [Validators.required, Validators.maxLength(150)]],
        password: ['', [Validators.required, Validators.maxLength(4096)]],
        keyencript: ['', [Validators.required, Validators.maxLength(4096)]],
        account: ['', [Validators.required, Validators.maxLength(4096)]],
        url: ['', [Validators.required, Validators.maxLength(2048), Validators.pattern(/^https:\/\/.+/i)]]
    });

    cargando = signal<boolean>(true);
    guardando = signal<boolean>(false);
    errorCarga = signal<string>('');

    ngOnInit(): void {
        this.cargarConfiguracion();
    }

    cargarConfiguracion(): void {
        this.cargando.set(true);
        this.errorCarga.set('');
        this.configuracionQrServicio.obtener().subscribe({
            next: (configuracion) => {
                this.formulario.patchValue({
                    userName: configuracion.userName,
                    password: configuracion.password,
                    keyencript: configuracion.keyencript,
                    account: configuracion.account,
                    url: configuracion.url
                });
                this.cargando.set(false);
            },
            error: () => {
                this.cargando.set(false);
                this.errorCarga.set('No fue posible cargar la configuración QR.');
            }
        });
    }

    guardar(): void {
        if (this.formulario.invalid) {
            this.formulario.markAllAsTouched();
            this.alertaServicio.advertencia('Complete los campos obligatorios antes de guardar.');
            return;
        }
        this.guardando.set(true);
        const valores = this.formulario.getRawValue();
        const solicitud: ConfiguracionQrSolicitud = {
            userName: valores.userName.trim(),
            password: valores.password,
            keyencript: valores.keyencript,
            account: valores.account,
            url: valores.url.trim()
        };
        this.configuracionQrServicio.actualizar(solicitud).subscribe({
            next: (configuracion) => {
                this.actualizarFormulario(configuracion);
                this.guardando.set(false);
                this.alertaServicio.exito('Configuración QR actualizada correctamente.');
            },
            error: (err) => {
                this.guardando.set(false);
                const mensaje = err?.error?.message || err?.error?.mensaje || 'No fue posible guardar la configuración QR.';
                this.alertaServicio.error(mensaje);
            }
        });
    }

    private actualizarFormulario(configuracion: ConfiguracionQr): void {
        this.formulario.patchValue({
            userName: configuracion.userName,
            password: configuracion.password,
            keyencript: configuracion.keyencript,
            account: configuracion.account,
            url: configuracion.url
        });
    }
}
