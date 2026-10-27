package bo.sipqr.servicio;

import bo.sipqr.modelo.PagoQr;
import bo.sipqr.modelo.baneco.RespuestaEstadoQr;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class PagoQrEstadoServicio {
    public static final String REGISTRADO = "REGISTRADO";
    public static final String GENERANDO = "GENERANDO";
    public static final String PENDIENTE = "PENDIENTE";
    public static final String PAGADO = "PAGADO";
    public static final String ANULADO = "ANULADO";
    public static final String VENCIDO = "VENCIDO";
    public static final String ERROR = "ERROR";

    public String determinarEstado(PagoQr pagoQr, RespuestaEstadoQr respuesta,
                                   BigDecimal totalPagado, LocalDate fechaReferencia) {
        Integer estadoBanco = respuesta == null ? null : respuesta.getStatusQrCode();
        if (estadoBanco == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "El banco no devolvió el estado del QR");
        }
        if (estadoBanco == 1) {
            return PAGADO;
        }
        if (estadoBanco == 9) {
            return ANULADO;
        }
        if (estadoBanco != 0) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "El banco devolvió un estado de QR no reconocido");
        }
        if (!Boolean.TRUE.equals(pagoQr.getSingleUse())
                && totalPagado != null && pagoQr.getAmount() != null
                && totalPagado.compareTo(pagoQr.getAmount()) >= 0) {
            return PAGADO;
        }
        if (pagoQr.getDueDate() != null && pagoQr.getDueDate().isBefore(fechaReferencia)) {
            return VENCIDO;
        }
        return PENDIENTE;
    }

    public boolean esEstadoFinal(String estado) {
        return PAGADO.equals(estado) || ANULADO.equals(estado) || VENCIDO.equals(estado) || ERROR.equals(estado);
    }
}
