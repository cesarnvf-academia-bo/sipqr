import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioServicio } from '../usuario-servicio';
import { Usuario, PaginaRespuesta } from '../usuario-modelo';
import { AlertaServicio } from '../../../compartido/servicios/alerta-servicio';
import { AutenticacionServicio } from '../../../nucleo/autenticacion/autenticacion-servicio';
import { Router, RouterLink } from '@angular/router';
import { UsuarioFormularioComponente } from '../usuario-formulario/usuario-formulario-componente';

@Component({
    selector: 'app-usuario-lista',
    standalone: true,
    imports: [CommonModule, FormsModule, UsuarioFormularioComponente, RouterLink],
    templateUrl: './usuario-lista-componente.html',
    styleUrls: ['./usuario-lista-componente.css']
})
export class UsuarioListaComponente implements OnInit {
    private usuarioServicio = inject(UsuarioServicio);
    private alertaServicio = inject(AlertaServicio);
    private autenticacionServicio = inject(AutenticacionServicio);
    private router = inject(Router);

    usuarios = signal<Usuario[]>([]);
    pagina = signal<number>(1);
    limite = signal<number>(5);
    buscar = signal<string>('');
    textoBuscar = signal<string>('');
    totalRegistros = signal<number>(0);
    totalPaginas = signal<number>(0);
    cargando = signal<boolean>(false);
    mostrarFormulario = signal<boolean>(false);
    usuarioEditar = signal<Usuario | null>(null);

    ngOnInit(): void {
        this.cargarUsuarios();
    }

    cargarUsuarios(): void {
        this.cargando.set(true);
        this.usuarioServicio.encontrarTodos(this.pagina(), this.limite(), this.buscar()).subscribe({
            next: (respuesta: PaginaRespuesta<Usuario>) => {
                this.usuarios.set(respuesta.datos);
                this.totalRegistros.set(respuesta.totalRegistros);
                this.totalPaginas.set(respuesta.totalPaginas);
                this.pagina.set(respuesta.pagina);
                this.limite.set(respuesta.limite);
                this.cargando.set(false);
            },
            error: () => {
                this.cargando.set(false);
                this.alertaServicio.error('No fue posible conectar con el servidor.');
            }
        });
    }

    buscarUsuarios(): void {
        this.buscar.set(this.textoBuscar());
        this.pagina.set(1);
        this.cargarUsuarios();
    }

    limpiarBusqueda(): void {
        this.textoBuscar.set('');
        this.buscar.set('');
        this.pagina.set(1);
        this.cargarUsuarios();
    }

    cambiarLimite(evento: Event): void {
        const valor = parseInt((evento.target as HTMLSelectElement).value, 10);
        this.limite.set(valor);
        this.pagina.set(1);
        this.cargarUsuarios();
    }

    cambiarPagina(nuevaPagina: number): void {
        if (nuevaPagina < 1 || nuevaPagina > this.totalPaginas()) {
            return;
        }
        this.pagina.set(nuevaPagina);
        this.cargarUsuarios();
    }

    abrirAdicionar(): void {
        this.usuarioEditar.set(null);
        this.mostrarFormulario.set(true);
    }

    abrirEditar(usuario: Usuario): void {
        this.usuarioEditar.set(usuario);
        this.mostrarFormulario.set(true);
    }

    cerrarFormulario(): void {
        this.mostrarFormulario.set(false);
        this.usuarioEditar.set(null);
    }

    alGuardar(): void {
        this.cerrarFormulario();
        this.cargarUsuarios();
    }

    async borrarUsuario(usuario: Usuario): Promise<void> {
        const confirmado = await this.alertaServicio.confirmar('¿Eliminar usuario?', '¿Está seguro de eliminar al usuario seleccionado? Esta acción no se puede deshacer.');
        if (!confirmado) {
            return;
        }
        this.usuarioServicio.borrar(usuario.id).subscribe({
            next: () => {
                this.alertaServicio.exito('Usuario eliminado correctamente.');
                if (this.usuarios().length === 1 && this.pagina() > 1) {
                    this.pagina.set(this.pagina() - 1);
                }
                this.cargarUsuarios();
            },
            error: (err) => {
                const mensaje = err?.error?.message || 'No fue posible eliminar el usuario.';
                this.alertaServicio.error(mensaje);
            }
        });
    }

    cerrarSesion(): void {
        this.autenticacionServicio.cerrarSesion();
        void this.router.navigate(['/acceso']);
    }

    get paginasArray(): number[] {
        const total = this.totalPaginas();
        if (total <= 0) {
            return [];
        }
        const paginas: number[] = [];
        for (let i = 1; i <= total; i++) {
            paginas.push(i);
        }
        return paginas;
    }
}
