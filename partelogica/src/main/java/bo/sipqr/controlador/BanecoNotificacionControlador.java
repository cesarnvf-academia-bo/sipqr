package bo.sipqr.controlador;

import bo.sipqr.modelo.baneco.NotificacionLoteSolicitud;
import bo.sipqr.modelo.baneco.NotificacionPagoQrSolicitud;
import bo.sipqr.modelo.baneco.RespuestaCallbackBaneco;
import bo.sipqr.servicio.baneco.BanecoNotificacionServicio;
import bo.sipqr.servicio.baneco.BanecoWebhookValidador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Notificaciones BANECO", description = "Callbacks enviados por el banco; no usan JWT propio")
public class BanecoNotificacionControlador {
    private final BanecoNotificacionServicio notificacionServicio;
    private final BanecoWebhookValidador webhookValidador;

    public BanecoNotificacionControlador(BanecoNotificacionServicio notificacionServicio,
                                         BanecoWebhookValidador webhookValidador) {
        this.notificacionServicio = notificacionServicio;
        this.webhookValidador = webhookValidador;
    }

    @PostMapping("/api/qrsimple/notifyPaymentQR")
    @Operation(summary = "Notificación de pago QR", description = "Valida el secreto compartido y registra el evento de forma idempotente")
    public ResponseEntity<RespuestaCallbackBaneco> notificarPagoQr(
            @RequestBody NotificacionPagoQrSolicitud solicitud, HttpServletRequest peticion) {
        webhookValidador.validar(peticion);
        return ResponseEntity.ok(notificacionServicio.recibirPagoQr(solicitud));
    }

    @PostMapping("/api/notifyStatus")
    @Operation(summary = "Notificación de estado de lote", description = "Valida el secreto compartido y registra el detalle de forma idempotente")
    public ResponseEntity<RespuestaCallbackBaneco> notificarEstadoLote(
            @RequestBody NotificacionLoteSolicitud solicitud, HttpServletRequest peticion) {
        webhookValidador.validar(peticion);
        return ResponseEntity.ok(notificacionServicio.recibirEstadoLote(solicitud));
    }
}
