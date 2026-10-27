export type EstadoPagoQr =
    | 'REGISTRADO'
    | 'GENERANDO'
    | 'PENDIENTE'
    | 'PAGADO'
    | 'ANULADO'
    | 'VENCIDO'
    | 'ERROR';

export interface PagoQr {
    id: number;
    transactionId: string;
    qrId: string | null;
    amount: number;
    currency: string;
    description: string | null;
    dueDate: string;
    singleUse: boolean;
    modifyAmount: boolean;
    branchCode: string | null;
    estado: EstadoPagoQr;
    qrImage: string | null;
    responseCode: string | null;
    responseMessage: string | null;
    resultadoIncierto: boolean;
    totalPagado: number;
    fechaCreacion: string;
    fechaActualizacion: string;
    fechaUltimaVerificacion: string | null;
    fechaPago: string | null;
    fechaAnulacion: string | null;
    puedeGenerar: boolean;
    puedeVerificar: boolean;
    puedeAnular: boolean;
    puedeBorrar: boolean;
    pagos?: PagoQrDetalle[];
}

export interface PagoQrDetalle {
    id: number;
    identificadorPagoBancario: string;
    fechaPago: string | null;
    importe: number;
    moneda: string;
    fechaRecepcion: string;
    datosPago: {
        senderName?: string;
        senderAccount?: string;
        senderBankCode?: string;
        description?: string;
        [clave: string]: unknown;
    };
}

export interface PagoQrSolicitud {
    amount: number;
    currency: string;
    description?: string;
    dueDate: string;
    singleUse: boolean;
    modifyAmount: boolean;
    branchCode?: string;
}

export interface PaginaPagoQr {
    datos: PagoQr[];
    pagina: number;
    limite: number;
    totalRegistros: number;
    totalPaginas: number;
}
