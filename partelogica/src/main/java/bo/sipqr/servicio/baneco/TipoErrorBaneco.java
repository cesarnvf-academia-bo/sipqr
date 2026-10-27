package bo.sipqr.servicio.baneco;

public enum TipoErrorBaneco {
    CONFIGURACION,
    VALIDACION,
    COMUNICACION,
    AUTENTICACION,
    RESPUESTA_INVALIDA,
    OPERACION_RECHAZADA,
    IDEMPOTENCIA,
    NO_ENCONTRADO,
    WEBHOOK
}
