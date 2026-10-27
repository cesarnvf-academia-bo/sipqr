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
import bo.sipqr.modelo.baneco.BanecoOperacion;
import bo.sipqr.modelo.baneco.BatchPayment;
import bo.sipqr.modelo.baneco.LotePagoSolicitud;
import bo.sipqr.modelo.baneco.RespuestaCargaPlanilla;
import bo.sipqr.repositorio.baneco.BanecoOperacionRepositorio;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.json.JsonMapper;

class BanecoLoteServicioPrueba {
    private BanecoClienteAutenticado cliente;
    private BanecoOperacionRepositorio repositorio;
    private BanecoLoteServicio servicio;

    @BeforeEach
    void preparar() {
        cliente = mock(BanecoClienteAutenticado.class);
        repositorio = mock(BanecoOperacionRepositorio.class);
        BanecoConfiguracionServicio configuracion = mock(BanecoConfiguracionServicio.class);
        when(configuracion.obtener()).thenReturn(new ConfiguracionBanco(
            "usuario", "clave", "llave", "cuenta-123", "https://apimktdesa.baneco.com.bo", OffsetDateTime.now()));
        when(repositorio.buscarOperacion(anyString(), anyString())).thenReturn(Optional.empty());
        doAnswer(invocacion -> {
            BanecoOperacion operacion = invocacion.getArgument(0);
            operacion.setId(1L);
            return null;
        }).when(repositorio).insertarOperacion(any(BanecoOperacion.class));
        servicio = new BanecoLoteServicio(cliente, configuracion,
            new BanecoRespuestaServicio(JsonMapper.builder().build()), new BanecoValidacionServicio(), repositorio);
    }

    @Test
    void cargaLoteConRutaDelContratoYCuentaPorDefecto() {
        when(cliente.postJson(eq("/api/batchPayment/upload"), any()))
            .thenReturn("{\"responseCode\":0,\"message\":\"\",\"bankBatchId\":9001}");

        RespuestaCargaPlanilla respuesta = servicio.cargarLote(solicitud(new BigDecimal("100.00")));

        assertEquals(9001L, respuesta.getBankBatchId());
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(cliente).postJson(eq("/api/batchPayment/upload"), captor.capture());
        LotePagoSolicitud enviado = (LotePagoSolicitud) captor.getValue();
        assertEquals("cuenta-123", enviado.getAccountCode());
        assertEquals("cuenta-123", enviado.getPaymentList().get(0).getAccountCode());
        assertEquals(1, enviado.getPaymentCount());
        ArgumentCaptor<BanecoOperacion> operacion = ArgumentCaptor.forClass(BanecoOperacion.class);
        verify(repositorio).actualizarOperacion(operacion.capture());
        assertEquals("EXITOSO", operacion.getValue().getEstado());
        assertEquals(9001L, operacion.getValue().getBancoBatchId());
    }

    @Test
    void rechazaSumaDistintaAlTotal() {
        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> servicio.cargarLote(solicitud(new BigDecimal("90.00"))));

        assertEquals(TipoErrorBaneco.VALIDACION, excepcion.getTipo());
        verify(cliente, never()).postJson(anyString(), any());
    }

    @Test
    void rechazaBatchIdYaUtilizado() {
        when(repositorio.buscarOperacion("LOTE_CARGAR", "LOTE-1"))
            .thenReturn(Optional.of(new BanecoOperacion()));

        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> servicio.cargarLote(solicitud(new BigDecimal("100.00"))));

        assertEquals(TipoErrorBaneco.IDEMPOTENCIA, excepcion.getTipo());
        verify(cliente, never()).postJson(anyString(), any());
    }

    private LotePagoSolicitud solicitud(BigDecimal total) {
        BatchPayment pago = new BatchPayment();
        pago.setBatchDetailId("DET-1");
        pago.setAmount(new BigDecimal("100.00"));
        pago.setAccountTypeCode("CA");
        pago.setBankCode("001");
        pago.setBeneficiaryName("Beneficiario");
        LotePagoSolicitud solicitud = new LotePagoSolicitud();
        solicitud.setBatchId("LOTE-1");
        solicitud.setType("PROVIDERS");
        solicitud.setDescripction("Planilla de prueba");
        solicitud.setDetailedDebit(Boolean.FALSE);
        solicitud.setBatchCurrency("BOB");
        solicitud.setBatchAmount(total);
        solicitud.setPaymentCount(1);
        solicitud.setPaymentList(List.of(pago));
        return solicitud;
    }
}
