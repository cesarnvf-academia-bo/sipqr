import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UsuarioServicio } from '../usuario-servicio';
import { Usuario, UsuarioActualizacion, UsuarioSolicitud } from '../usuario-modelo';
import { AlertaServicio } from '../../../compartido/servicios/alerta-servicio';

@Component({
    selector: 'app-usuario-formulario',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './usuario-formulario-componente.html',
    styleUrl: './usuario-formulario-componente.css'
})
export class UsuarioFormularioComponente implements OnInit {
    private fb = inject(FormBuilder);
    private usuarioServicio = inject(UsuarioServicio);
    private alertaServicio = inject(AlertaServicio);

    @Input() usuario: Usuario | null = null;
    @Output() cerrar = new EventEmitter<void>();
    @Output() guardado = new EventEmitter<void>();

    formulario!: FormGroup;
    esEdicion = false;

    ngOnInit(): void {
        this.esEdicion = !!this.usuario;
        this.formulario = this.fb.group({
            usuario: [this.usuario?.usuario || '', [Validators.required, Validators.minLength(4), Validators.maxLength(100)]],
            clave: ['', this.esEdicion ? [] : [Validators.required, Validators.minLength(12), Validators.maxLength(72)]],
            estado: [this.usuario?.estado ?? true, [Validators.required]]
        });
    }

    guardar(): void {
        if (this.formulario.invalid) {
            this.formulario.markAllAsTouched();
            return;
        }
        const valores = this.formulario.value;
        if (this.esEdicion && this.usuario) {
            const actualizacion: UsuarioActualizacion = {
                usuario: valores.usuario.trim(),
                estado: valores.estado
            };
            if (valores.clave && valores.clave.trim() !== '') {
                actualizacion.clave = valores.clave;
            }
            this.usuarioServicio.actualizar(this.usuario.id, actualizacion).subscribe({
                next: () => {
                    this.alertaServicio.exito('Usuario actualizado correctamente.');
                    this.guardado.emit();
                },
                error: (err) => {
                    const mensaje = err?.error?.message || 'No fue posible actualizar el usuario.';
                    this.alertaServicio.error(mensaje);
                }
            });
        } else {
            const solicitud: UsuarioSolicitud = {
                usuario: valores.usuario.trim(),
                clave: valores.clave,
                estado: valores.estado
            };
            this.usuarioServicio.adicionar(solicitud).subscribe({
                next: () => {
                    this.alertaServicio.exito('Usuario registrado correctamente.');
                    this.guardado.emit();
                },
                error: (err) => {
                    const mensaje = err?.error?.message || 'No fue posible registrar el usuario.';
                    if (mensaje.toLowerCase().includes('ya está registrado')) {
                        this.alertaServicio.error('El nombre de usuario ya está registrado.');
                    } else {
                        this.alertaServicio.error(mensaje);
                    }
                }
            });
        }
    }

    cancelar(): void {
        this.cerrar.emit();
    }
}
