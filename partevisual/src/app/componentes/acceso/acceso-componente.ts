import { Component, OnInit, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AutenticacionServicio } from '../../nucleo/autenticacion/autenticacion-servicio';

@Component({
    selector: 'app-acceso',
    standalone: true,
    imports: [ReactiveFormsModule, RouterLink],
    templateUrl: './acceso-componente.html',
    styleUrl: './acceso-componente.css'
})
export class AccesoComponente implements OnInit {
    private formularioBuilder = inject(NonNullableFormBuilder);
    private autenticacionServicio = inject(AutenticacionServicio);
    private router = inject(Router);

    formulario = this.formularioBuilder.group({
        usuario: ['', [Validators.required, Validators.maxLength(100)]],
        clave: ['', [Validators.required, Validators.maxLength(72)]]
    });

    enviando = signal<boolean>(false);
    error = signal<string>('');

    ngOnInit(): void {
        if (this.autenticacionServicio.autenticado()) {
            void this.router.navigate(['/usuarios']);
        }
    }

    ingresar(): void {
        if (this.formulario.invalid) {
            this.formulario.markAllAsTouched();
            return;
        }
        this.enviando.set(true);
        this.error.set('');
        const valores = this.formulario.getRawValue();
        this.autenticacionServicio.iniciarSesion(valores.usuario.trim(), valores.clave).subscribe({
            next: () => {
                this.enviando.set(false);
                void this.router.navigate(['/usuarios']);
            },
            error: () => {
                this.enviando.set(false);
                this.error.set('Credenciales inválidas o usuario inactivo.');
            }
        });
    }
}
