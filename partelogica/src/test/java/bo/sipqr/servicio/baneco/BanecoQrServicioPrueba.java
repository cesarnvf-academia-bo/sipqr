package bo.sipqr.servicio.baneco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bo.sipqr.cliente.baneco.BanecoClienteAutenticado;
import bo.sipqr.dto.baneco.GenerarQrSolicitud;
import bo.sipqr.modelo.baneco.BanecoOperacion;
import bo.sipqr.modelo.baneco.RespuestaGeneracionQr;
import bo.sipqr.repositorio.baneco.BanecoOperacionRepositorio;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.json.JsonMapper;

class BanecoQrServicioPrueba {
    private BanecoClienteAutenticado cliente;
    private BanecoCifradoServicio cifrado;
    private BanecoConfiguracionServicio configuracion;
    private BanecoOperacionRepositorio repositorio;
    private BanecoQrServicio servicio;

    @BeforeEach
    void preparar() {
        cliente = mock(BanecoClienteAutenticado.class);
        cifrado = mock(BanecoCifradoServicio.class);
        configuracion = mock(BanecoConfiguracionServicio.class);
        repositorio = mock(BanecoOperacionRepositorio.class);
        when(configuracion.obtener()).thenReturn(new ConfiguracionBanco(
            "usuario", "clave", "llave", "cuenta-123", "https://apimktdesa.baneco.com.bo", OffsetDateTime.now()));
        when(cifrado.encriptar("cuenta-123")).thenReturn("CUENTA-CIFRADA");
        when(repositorio.buscarOperacion(anyString(), anyString())).thenReturn(Optional.empty());
        doAnswer(invocacion -> {
            BanecoOperacion operacion = invocacion.getArgument(0);
            operacion.setId(1L);
            return null;
        }).when(repositorio).insertarOperacion(any(BanecoOperacion.class));
        servicio = new BanecoQrServicio(cliente, cifrado, configuracion,
            new BanecoRespuestaServicio(JsonMapper.builder().build()), new BanecoValidacionServicio(), repositorio);
    }

    @Test
    void generaQrConCuentaCifradaYRegistraOperacion() {
        when(cliente.postJson(eq("/api/qrsimple/generateQR"), any()))
            .thenReturn("{\"responseCode\":0,\"qrId\":\"QR-1\",\"qrImage\":\"BASE64\"}");

        RespuestaGeneracionQr respuesta = servicio.generarQr(solicitud("TX-1", new BigDecimal("15.50")));

        assertEquals("QR-1", respuesta.getQrId());
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(cliente).postJson(eq("/api/qrsimple/generateQR"), captor.capture());
        @SuppressWarnings("unchecked")
        Map<String, Object> cuerpo = (Map<String, Object>) captor.getValue();
        assertEquals("CUENTA-CIFRADA", cuerpo.get("accountCredit"));
        assertEquals("TX-1", cuerpo.get("transactionId"));
        ArgumentCaptor<BanecoOperacion> operacion = ArgumentCaptor.forClass(BanecoOperacion.class);
        verify(repositorio).actualizarOperacion(operacion.capture());
        assertEquals("EXITOSO", operacion.getValue().getEstado());
        assertEquals("QR-1", operacion.getValue().getQrId());
    }

    @Test
    void rechazaTransactionIdYaUtilizado() {
        BanecoOperacion existente = new BanecoOperacion();
        existente.setEstado("EXITOSO");
        when(repositorio.buscarOperacion("QR_GENERAR", "TX-1")).thenReturn(Optional.of(existente));

        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> servicio.generarQr(solicitud("TX-1", new BigDecimal("15.50"))));

        assertEquals(TipoErrorBaneco.IDEMPOTENCIA, excepcion.getTipo());
        verify(cliente, never()).postJson(anyString(), any());
    }

    @Test
    void rechazaImporteInvalidoAntesDeLlamarAlBanco() {
        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> servicio.generarQr(solicitud("TX-2", BigDecimal.ZERO)));

        assertEquals(TipoErrorBaneco.VALIDACION, excepcion.getTipo());
        verify(cliente, never()).postJson(anyString(), any());
    }

    @Test
    void anulaQrYRegistraOperacion() {
        when(cliente.deleteJson(eq("/api/qrsimple/cancelQR"), any()))
            .thenReturn("{\"responseCode\":0,\"message\":\"\"}");

        servicio.anularQr("QR-1");

        verify(cliente).deleteJson(eq("/api/qrsimple/cancelQR"), any());
        ArgumentCaptor<BanecoOperacion> operacion = ArgumentCaptor.forClass(BanecoOperacion.class);
        verify(repositorio).actualizarOperacion(operacion.capture());
        assertEquals("QR_ANULAR", operacion.getValue().getTipo());
        assertEquals("EXITOSO", operacion.getValue().getEstado());
    }

    private GenerarQrSolicitud solicitud(String transactionId, BigDecimal importe) {
        GenerarQrSolicitud solicitud = new GenerarQrSolicitud();
        solicitud.setTransactionId(transactionId);
        solicitud.setCurrency("BOB");
        solicitud.setAmount(importe);
        solicitud.setDueDate("2026-12-31");
        solicitud.setSingleUse(Boolean.TRUE);
        solicitud.setModifyAmount(Boolean.FALSE);
        solicitud.setDescription("Pago de prueba");
        return solicitud;
    }
}
