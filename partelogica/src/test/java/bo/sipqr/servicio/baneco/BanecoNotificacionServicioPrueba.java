package bo.sipqr.servicio.baneco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bo.sipqr.modelo.baneco.NotificacionLoteSolicitud;
import bo.sipqr.modelo.baneco.NotificacionPagoQrSolicitud;
import bo.sipqr.modelo.baneco.PaymentQR;
import bo.sipqr.modelo.baneco.RespuestaCallbackBaneco;
import bo.sipqr.repositorio.baneco.BanecoOperacionRepositorio;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class BanecoNotificacionServicioPrueba {
    private BanecoOperacionRepositorio repositorio;
    private BanecoNotificacionServicio servicio;

    @BeforeEach
    void preparar() {
        repositorio = mock(BanecoOperacionRepositorio.class);
        servicio = new BanecoNotificacionServicio(repositorio,
            new BanecoRespuestaServicio(JsonMapper.builder().build()), new BanecoValidacionServicio());
    }

    @Test
    void registraPagoQrNuevo() {
        when(repositorio.registrarNotificacion(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        RespuestaCallbackBaneco respuesta = servicio.recibirPagoQr(pagoQr());

        assertEquals(0, respuesta.getResponseCode());
        verify(repositorio).registrarNotificacion(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void respondeExitoSinDuplicarPagoQr() {
        when(repositorio.registrarNotificacion(anyString(), anyString(), anyString(), anyString())).thenReturn(false);

        RespuestaCallbackBaneco respuesta = servicio.recibirPagoQr(pagoQr());

        assertEquals(0, respuesta.getResponseCode());
    }

    @Test
    void rechazaPagoQrSinImporte() {
        NotificacionPagoQrSolicitud solicitud = pagoQr();
        solicitud.getPayment().setAmount(null);

        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class, () -> servicio.recibirPagoQr(solicitud));

        assertEquals(TipoErrorBaneco.VALIDACION, excepcion.getTipo());
        verify(repositorio, never()).registrarNotificacion(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void registraDetalleDeLoteAceptado() {
        when(repositorio.registrarNotificacion(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        RespuestaCallbackBaneco respuesta = servicio.recibirEstadoLote(lote("ACEP"));

        assertEquals(0, respuesta.getResponseCode());
        verify(repositorio).guardarDetalleLote(any());
    }

    @Test
    void rechazaEstadoDeLoteDesconocido() {
        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> servicio.recibirEstadoLote(lote("OTRO")));

        assertEquals(TipoErrorBaneco.VALIDACION, excepcion.getTipo());
        verify(repositorio, never()).guardarDetalleLote(any());
    }

    private NotificacionPagoQrSolicitud pagoQr() {
        PaymentQR pago = new PaymentQR();
        pago.setQrId("QR-1");
        pago.setTransactionId("TX-1");
        pago.setAmount(new BigDecimal("10.00"));
        pago.setCurrency("BOB");
        NotificacionPagoQrSolicitud solicitud = new NotificacionPagoQrSolicitud();
        solicitud.setPayment(pago);
        return solicitud;
    }

    private NotificacionLoteSolicitud lote(String estado) {
        NotificacionLoteSolicitud solicitud = new NotificacionLoteSolicitud();
        solicitud.setBankBatchId(100L);
        solicitud.setBatchId("LOTE-1");
        solicitud.setBatchDetailId("DET-1");
        solicitud.setStatus(estado);
        solicitud.setDescriptionStatus("Descripción");
        solicitud.setTransactionIdDebit(200L);
        solicitud.setTransactionIdCredit("CR-1");
        return solicitud;
    }
}
