package bo.sipqr.servicio.baneco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bo.sipqr.modelo.baneco.RespuestaEstadoQr;
import bo.sipqr.modelo.baneco.RespuestaGeneracionQr;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class BanecoRespuestaServicioPrueba {
    private final BanecoRespuestaServicio servicio = new BanecoRespuestaServicio(JsonMapper.builder().build());

    @Test
    void leeAliasDeEstadoYConviertePagoUnicoEnLista() {
        String json = "{\"responseCode\":0,\"statusQRCode\":1,"
            + "\"payment\":{\"qrId\":\"QR-1\",\"transactionId\":\"TX-1\",\"amount\":10.50}}";

        RespuestaEstadoQr respuesta = servicio.leerYValidar(json, RespuestaEstadoQr.class);

        assertEquals(1, respuesta.getStatusQrCode());
        assertEquals(1, respuesta.getPayment().size());
        assertEquals("QR-1", respuesta.getPayment().get(0).getQrId());
        assertEquals(new BigDecimal("10.50"), respuesta.getPayment().get(0).getAmount());
    }

    @Test
    void aceptaAliasEnMinuscula() {
        String json = "{\"responseCode\":0,\"statusQrCode\":9,\"payment\":[]}";

        RespuestaEstadoQr respuesta = servicio.leerYValidar(json, RespuestaEstadoQr.class);

        assertEquals(9, respuesta.getStatusQrCode());
        assertTrue(respuesta.getPayment().isEmpty());
    }

    @Test
    void validaRespuestaExitosa() {
        RespuestaGeneracionQr respuesta = servicio.leerYValidar(
            "{\"responseCode\":0,\"qrId\":\"QR-1\",\"qrImage\":\"BASE64\"}", RespuestaGeneracionQr.class);

        assertEquals("QR-1", respuesta.getQrId());
    }

    @Test
    void rechazaCodigoBancarioDistintoDeCero() {
        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> servicio.leerYValidar("{\"responseCode\":12,\"message\":\"Rechazado\"}", RespuestaGeneracionQr.class));

        assertEquals(TipoErrorBaneco.OPERACION_RECHAZADA, excepcion.getTipo());
        assertEquals(12, excepcion.getCodigoBanco());
    }

    @Test
    void rechazaJsonInvalido() {
        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> servicio.leerYValidar("no-es-json", RespuestaGeneracionQr.class));

        assertEquals(TipoErrorBaneco.RESPUESTA_INVALIDA, excepcion.getTipo());
    }

    @Test
    void calculaHashSha256() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            servicio.hash("abc"));
    }
}
