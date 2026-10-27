package bo.sipqr.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bo.sipqr.dto.PagoQrRespuesta;
import bo.sipqr.dto.PagoQrSolicitud;
import bo.sipqr.dto.baneco.GenerarQrSolicitud;
import bo.sipqr.modelo.PagoQr;
import bo.sipqr.modelo.baneco.PaymentQR;
import bo.sipqr.modelo.baneco.RespuestaEstadoQr;
import bo.sipqr.modelo.baneco.RespuestaGeneracionQr;
import bo.sipqr.repositorio.PagoQrDetalleRepositorio;
import bo.sipqr.repositorio.PagoQrRepositorio;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.BanecoQrServicio;
import bo.sipqr.servicio.impl.PagoQrServicioImpl;
import bo.sipqr.servicio.baneco.BanecoRespuestaServicio;
import bo.sipqr.servicio.baneco.BanecoValidacionServicio;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

class PagoQrServicioImplPrueba {
    private PagoQrRepositorio pagoQrRepositorio;
    private PagoQrDetalleRepositorio detalleRepositorio;
    private BanecoQrServicio banecoQrServicio;
    private PagoQrServicioImpl servicio;

    @BeforeEach
    void preparar() {
        pagoQrRepositorio = mock(PagoQrRepositorio.class);
        detalleRepositorio = mock(PagoQrDetalleRepositorio.class);
        banecoQrServicio = mock(BanecoQrServicio.class);
        BanecoValidacionServicio banecoValidacion = new BanecoValidacionServicio();
        JsonMapper jsonMapper = JsonMapper.builder().build();
        servicio = new PagoQrServicioImpl(pagoQrRepositorio, detalleRepositorio, banecoQrServicio,
            new PagoQrValidacionServicio(banecoValidacion), new PagoQrEstadoServicio(),
            banecoValidacion, new BanecoRespuestaServicio(jsonMapper), jsonMapper);
    }

    @Test
    void generaQrYGuardaImagenValidada() {
        AtomicReference<PagoQr> guardado = new AtomicReference<PagoQr>();
        doAnswer(invocacion -> {
            PagoQr pago = invocacion.getArgument(0);
            pago.setId(1L);
            guardado.set(pago);
            return 1;
        }).when(pagoQrRepositorio).insertar(any(PagoQr.class));
        when(pagoQrRepositorio.marcarGenerando(1L)).thenReturn(1);
        when(banecoQrServicio.generarQr(any(GenerarQrSolicitud.class))).thenReturn(respuestaGeneracion());
        doAnswer(invocacion -> {
            guardado.set(invocacion.getArgument(0));
            return 1;
        }).when(pagoQrRepositorio).actualizarGeneracion(any(PagoQr.class));
        when(pagoQrRepositorio.encontrarPorId(1L)).thenAnswer(invocacion -> Optional.of(guardado.get()));
        when(detalleRepositorio.encontrarPorPagoQrId(1L)).thenReturn(List.of());

        PagoQrRespuesta respuesta = servicio.generar(solicitud());

        assertEquals("PENDIENTE", respuesta.getEstado());
        assertEquals("QR-1", respuesta.getQrId());
        assertNotNull(respuesta.getQrImage());
        assertEquals(Boolean.FALSE, respuesta.getResultadoIncierto());
        ArgumentCaptor<GenerarQrSolicitud> captor = ArgumentCaptor.forClass(GenerarQrSolicitud.class);
        verify(banecoQrServicio).generarQr(captor.capture());
        assertEquals(true, captor.getValue().getTransactionId().startsWith("PGQ-"));
    }

    @Test
    void marcaResultadoInciertoCuandoFallaLaComunicacion() {
        doAnswer(invocacion -> {
            PagoQr pago = invocacion.getArgument(0);
            pago.setId(2L);
            return 1;
        }).when(pagoQrRepositorio).insertar(any(PagoQr.class));
        when(pagoQrRepositorio.marcarGenerando(2L)).thenReturn(1);
        when(banecoQrServicio.generarQr(any(GenerarQrSolicitud.class)))
            .thenThrow(new BanecoExcepcion(TipoErrorBaneco.COMUNICACION, "Tiempo de espera agotado"));

        assertThrows(BanecoExcepcion.class, () -> servicio.generar(solicitud()));

        verify(pagoQrRepositorio).actualizarError(eq(2L), isNull(), isNull(),
            eq("Tiempo de espera agotado"), eq(true));
    }

    @Test
    void conservaQrIdCuandoLaImagenBancariaEsInvalida() {
        doAnswer(invocacion -> {
            PagoQr pago = invocacion.getArgument(0);
            pago.setId(3L);
            return 1;
        }).when(pagoQrRepositorio).insertar(any(PagoQr.class));
        when(pagoQrRepositorio.marcarGenerando(3L)).thenReturn(1);
        RespuestaGeneracionQr respuesta = respuestaGeneracion();
        respuesta.setQrImage("no-es-png");
        when(banecoQrServicio.generarQr(any(GenerarQrSolicitud.class))).thenReturn(respuesta);

        assertThrows(BanecoExcepcion.class, () -> servicio.generar(solicitud()));

        verify(pagoQrRepositorio).actualizarError(eq(3L), eq("QR-1"), isNull(),
            eq("La imagen del QR no es un Base64 válido"), eq(true));
    }

    @Test
    void verificacionMarcaPagadoEImportaPago() {
        PagoQr pago = pagoPendiente(1L, "QR-1", true, new BigDecimal("100.00"));
        when(pagoQrRepositorio.encontrarPorId(1L)).thenReturn(Optional.of(pago));
        when(banecoQrServicio.verificarEstadoQr("QR-1")).thenReturn(respuestaEstado(1));
        when(detalleRepositorio.sumarImportes(1L)).thenReturn(new BigDecimal("100.00"));
        when(detalleRepositorio.encontrarPorPagoQrId(1L)).thenReturn(List.of());
        when(pagoQrRepositorio.actualizarVerificacion(any(PagoQr.class))).thenReturn(1);

        PagoQrRespuesta respuesta = servicio.verificar(1L);

        assertEquals("PAGADO", respuesta.getEstado());
        assertNotNull(respuesta.getFechaPago());
        verify(detalleRepositorio).insertarSiNoExiste(any());
        verify(pagoQrRepositorio).actualizarVerificacion(pago);
    }

    @Test
    void multiusoConPagoParcialPermanecePendiente() {
        PagoQr pago = pagoPendiente(1L, "QR-1", false, new BigDecimal("100.00"));
        pago.setDueDate(LocalDate.of(2099, 12, 31));
        when(pagoQrRepositorio.encontrarPorId(1L)).thenReturn(Optional.of(pago));
        when(banecoQrServicio.verificarEstadoQr("QR-1")).thenReturn(respuestaEstado(0));
        when(detalleRepositorio.sumarImportes(1L)).thenReturn(new BigDecimal("40.00"));
        when(detalleRepositorio.encontrarPorPagoQrId(1L)).thenReturn(List.of());
        when(pagoQrRepositorio.actualizarVerificacion(any(PagoQr.class))).thenReturn(1);

        PagoQrRespuesta respuesta = servicio.verificar(1L);

        assertEquals("PENDIENTE", respuesta.getEstado());
    }

    @Test
    void noAnulaUnPagoPagado() {
        PagoQr pago = pagoPendiente(1L, "QR-1", true, new BigDecimal("100.00"));
        pago.setEstado("PAGADO");
        when(pagoQrRepositorio.encontrarPorId(1L)).thenReturn(Optional.of(pago));

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
            () -> servicio.anular(1L));

        assertEquals(HttpStatus.CONFLICT, excepcion.getStatusCode());
        verify(banecoQrServicio, never()).anularQr(any());
    }

    @Test
    void soloBorraRegistradoSinQr() {
        PagoQr pago = pagoPendiente(1L, "QR-1", true, new BigDecimal("100.00"));
        when(pagoQrRepositorio.encontrarPorId(1L)).thenReturn(Optional.of(pago));

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
            () -> servicio.borrar(1L));

        assertEquals(HttpStatus.CONFLICT, excepcion.getStatusCode());
        verify(pagoQrRepositorio, never()).borrarRegistrado(anyLong());
    }

    private PagoQrSolicitud solicitud() {
        PagoQrSolicitud solicitud = new PagoQrSolicitud();
        solicitud.setAmount(new BigDecimal("100.00"));
        solicitud.setCurrency("BOB");
        solicitud.setDescription("Pago de prueba");
        solicitud.setDueDate(LocalDate.of(2099, 12, 31));
        solicitud.setSingleUse(Boolean.TRUE);
        solicitud.setModifyAmount(Boolean.FALSE);
        return solicitud;
    }

    private RespuestaGeneracionQr respuestaGeneracion() {
        RespuestaGeneracionQr respuesta = new RespuestaGeneracionQr();
        respuesta.setResponseCode(0);
        respuesta.setQrId("QR-1");
        respuesta.setQrImage(Base64.getEncoder().encodeToString(new byte[] {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
        }));
        return respuesta;
    }

    private RespuestaEstadoQr respuestaEstado(Integer estado) {
        PaymentQR pago = new PaymentQR();
        pago.setQrId("QR-1");
        pago.setTransactionId("PAGO-1");
        pago.setCurrency("BOB");
        pago.setAmount(new BigDecimal("100.00"));
        pago.setPaymentDate("2026-10-26T10:00:00-04:00");
        RespuestaEstadoQr respuesta = new RespuestaEstadoQr();
        respuesta.setResponseCode(0);
        respuesta.setStatusQrCode(estado);
        respuesta.setPayment(List.of(pago));
        return respuesta;
    }

    private PagoQr pagoPendiente(Long id, String qrId, boolean singleUse, BigDecimal amount) {
        PagoQr pago = new PagoQr();
        pago.setId(id);
        pago.setQrId(qrId);
        pago.setTransactionId("PGQ-1");
        pago.setAmount(amount);
        pago.setCurrency("BOB");
        pago.setDueDate(LocalDate.of(2099, 12, 31));
        pago.setSingleUse(singleUse);
        pago.setEstado("PENDIENTE");
        pago.setResultadoIncierto(Boolean.FALSE);
        return pago;
    }
}
