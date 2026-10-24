export interface Usuario {
    id: number;
    usuario: string;
    estado: boolean;
}

export interface UsuarioSolicitud {
    usuario: string;
    clave: string;
    estado: boolean;
}

export interface UsuarioActualizacion {
    usuario: string;
    clave?: string;
    estado: boolean;
}

export interface PaginaRespuesta<T> {
    datos: T[];
    pagina: number;
    limite: number;
    totalRegistros: number;
    totalPaginas: number;
}
