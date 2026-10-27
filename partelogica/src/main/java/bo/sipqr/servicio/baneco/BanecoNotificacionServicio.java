package bo.sipqr.servicio.baneco;

import bo.sipqr.modelo.baneco.BanecoDetalleLote;
import bo.sipqr.modelo.baneco.NotificacionLoteSolicitud;
import bo.sipqr.modelo.baneco.NotificacionPagoQrSolicitud;
import bo.sipqr.modelo.baneco.PaymentQR;
import bo.sipqr.modelo.baneco.RespuestaCallbackBaneco;
import bo.sipqr.repositorio.baneco.BanecoOperacionRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BanecoNotificacionServicio {
    private static final Logger REGISTRO = LoggerFactory.getLogger(BanecoNotificacionServicio.class);
    private static final String TIPO_PAGO_QR = "PAGO_QR";
    private static final String TIPO_ESTADO_LOTE = "LOTE_ESTADO";

    private final BanecoOperacionRepositorio operacionRepositorio;
    private final BanecoRespuestaServicio respuestaServicio;
    private final BanecoValidacionServicio validacionServicio;

    public BanecoNotificacionServicio(BanecoOperacionRepositorio operacionRepositorio,
                                      BanecoRespuestaServicio respuestaServicio,
                                      BanecoValidacionServicio validacionServicio) {
        this.operacionRepositorio = operacionRepositorio;
        this.respuestaServicio = respuestaServicio;
        this.validacionServicio = validacionServicio;
    }

    public RespuestaCallbackBaneco recibirPagoQr(NotificacionPagoQrSolicitud solicitud) {
        PaymentQR pago = solicitud.getPayment();
        if (pago == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "La notificación no contiene los datos del pago");
        }
        String qrId = validacionServicio.textoRequerido(pago.getQrId(), "El identificador del QR", 100);
        String transactionId = validacionServicio.textoRequerido(pago.getTransactionId(),
            "El identificador de transacción", 150);
        validacionServicio.importe(pago.getAmount(), "El importe notificado");
        String clave = construirClave("QR", qrId, transactionId);
        String hash = respuestaServicio.hash(respuestaServicio.serializar(solicitud));
        boolean registrada = operacionRepositorio.registrarNotificacion(TIPO_PAGO_QR, clave, hash, "RECIBIDA");
        if (!registrada) {
            REGISTRO.info("BANECO notificación de pago duplicada para QR {}", qrId);
        } else {
            REGISTRO.info("BANECO notificación de pago registrada para QR {}", qrId);
        }
        return new RespuestaCallbackBaneco(0, "");
    }

    public RespuestaCallbackBaneco recibirEstadoLote(NotificacionLoteSolicitud solicitud) {
        if (solicitud.getBankBatchId() == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "La notificación no contiene el identificador bancario del lote");
        }
        String batchId = validacionServicio.textoRequerido(solicitud.getBatchId(),
            "El identificador del lote", 150);
        String batchDetailId = validacionServicio.textoRequerido(solicitud.getBatchDetailId(),
            "El identificador de detalle", 150);
        String estado = validacionServicio.textoRequerido(solicitud.getStatus(),
            "El estado del detalle", 10).toUpperCase();
        if (!estado.equals("ACEP") && !estado.equals("RECH")) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "El estado del detalle debe ser ACEP o RECH");
        }
        String clave = construirClave("LOTE", String.valueOf(solicitud.getBankBatchId()), batchId, batchDetailId);
        String hash = respuestaServicio.hash(respuestaServicio.serializar(solicitud));
        boolean registrada = operacionRepositorio.registrarNotificacion(TIPO_ESTADO_LOTE, clave, hash, estado);
        if (!registrada) {
            REGISTRO.info("BANECO notificación de lote duplicada {}", clave);
        }
        BanecoDetalleLote detalle = new BanecoDetalleLote();
        detalle.setBancoBatchId(solicitud.getBankBatchId());
        detalle.setBatchId(batchId);
        detalle.setBatchDetailId(batchDetailId);
        detalle.setEstado(estado);
        detalle.setDescripcionEstado(solicitud.getDescriptionStatus());
        detalle.setTransactionIdDebito(solicitud.getTransactionIdDebit() == null
            ? null : String.valueOf(solicitud.getTransactionIdDebit()));
        detalle.setTransactionIdCredito(solicitud.getTransactionIdCredit());
        operacionRepositorio.guardarDetalleLote(detalle);
        return new RespuestaCallbackBaneco(0, "");
    }

    private String construirClave(String prefijo, String... partes) {
        StringBuilder clave = new StringBuilder(prefijo);
        for (String parte : partes) {
            clave.append('|').append(parte);
        }
        if (clave.length() <= 255) {
            return clave.toString();
        }
        return prefijo + "|" + respuestaServicio.hash(clave.toString());
    }
}
