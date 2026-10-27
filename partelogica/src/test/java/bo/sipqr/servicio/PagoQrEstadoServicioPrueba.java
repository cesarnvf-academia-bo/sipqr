package bo.sipqr.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import bo.sipqr.modelo.PagoQr;
import bo.sipqr.modelo.baneco.RespuestaEstadoQr;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PagoQrEstadoServicioPrueba {
    private PagoQrEstadoServicio servicio;
    private PagoQr pagoQr;

    @BeforeEach
    void preparar() {
        servicio = new PagoQrEstadoServicio();
        pagoQr = new PagoQr();
        pagoQr.setAmount(new BigDecimal("100.00"));
        pagoQr.setDueDate(LocalDate.of(2026, 10, 26));
        pagoQr.setSingleUse(Boolean.FALSE);
    }

    @Test
    void estadoUnoMarcaPagado() {
        assertEquals(PagoQrEstadoServicio.PAGADO,
            servicio.determinarEstado(pagoQr, respuesta(1), BigDecimal.ZERO, LocalDate.of(2026, 10, 26)));
    }

    @Test
    void estadoNueveMarcaAnulado() {
        assertEquals(PagoQrEstadoServicio.ANULADO,
            servicio.determinarEstado(pagoQr, respuesta(9), BigDecimal.ZERO, LocalDate.of(2026, 10, 26)));
    }

    @Test
    void multiusoCompletaImporteYMarcaPagado() {
        assertEquals(PagoQrEstadoServicio.PAGADO,
            servicio.determinarEstado(pagoQr, respuesta(0), new BigDecimal("100.00"), LocalDate.of(2026, 10, 26)));
    }

    @Test
    void multiusoParcialPermanecePendiente() {
        assertEquals(PagoQrEstadoServicio.PENDIENTE,
            servicio.determinarEstado(pagoQr, respuesta(0), new BigDecimal("40.00"), LocalDate.of(2026, 10, 26)));
    }

    @Test
    void vencidoNoSeAnulaAutomaticamente() {
        assertEquals(PagoQrEstadoServicio.VENCIDO,
            servicio.determinarEstado(pagoQr, respuesta(0), BigDecimal.ZERO, LocalDate.of(2026, 10, 27)));
    }

    @Test
    void rechazaEstadoBancarioDesconocido() {
        assertThrows(BanecoExcepcion.class,
            () -> servicio.determinarEstado(pagoQr, respuesta(5), BigDecimal.ZERO, LocalDate.of(2026, 10, 26)));
    }

    private RespuestaEstadoQr respuesta(Integer estado) {
        RespuestaEstadoQr respuesta = new RespuestaEstadoQr();
        respuesta.setStatusQrCode(estado);
        return respuesta;
    }
}
