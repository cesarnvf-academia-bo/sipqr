package bo.sipqr.servicio.baneco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bo.sipqr.cliente.baneco.BanecoClienteAutenticado;
import bo.sipqr.dto.baneco.ConsultaMovimientosSolicitud;
import bo.sipqr.modelo.baneco.RespuestaMovimientosCuenta;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.json.JsonMapper;

class BanecoCuentaServicioPrueba {
    private BanecoClienteAutenticado cliente;
    private BanecoCifradoServicio cifrado;
    private BanecoCuentaServicio servicio;

    @BeforeEach
    void preparar() {
        cliente = mock(BanecoClienteAutenticado.class);
        cifrado = mock(BanecoCifradoServicio.class);
        BanecoConfiguracionServicio configuracion = mock(BanecoConfiguracionServicio.class);
        when(configuracion.obtener()).thenReturn(new ConfiguracionBanco(
            "usuario", "clave", "llave", "cuenta-123", "https://apimktdesa.baneco.com.bo", OffsetDateTime.now()));
        when(cifrado.encriptar("cuenta-123")).thenReturn("CUENTA-CIFRADA");
        servicio = new BanecoCuentaServicio(cliente, cifrado, configuracion,
            new BanecoRespuestaServicio(JsonMapper.builder().build()), new BanecoValidacionServicio());
    }

    @Test
    void consultaMovimientosConRutaYCuentaCifradaDelContrato() {
        when(cliente.postJson(eq("/api/accounts/queryMovements"), any()))
            .thenReturn("{\"responseCode\":0,\"message\":\"\",\"accountDetailList\":[],\"accountWithheldList\":[]}");

        RespuestaMovimientosCuenta respuesta = servicio.consultarMovimientos(solicitud());

        assertEquals(0, respuesta.getResponseCode());
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(cliente).postJson(eq("/api/accounts/queryMovements"), captor.capture());
        @SuppressWarnings("unchecked")
        Map<String, Object> cuerpo = (Map<String, Object>) captor.getValue();
        assertEquals("CUENTA-CIFRADA", cuerpo.get("accountCode"));
        assertEquals("2026-01-01", cuerpo.get("startDate"));
        assertEquals("2026-01-31", cuerpo.get("endDate"));
    }

    @Test
    void rechazaRangoInvertido() {
        ConsultaMovimientosSolicitud solicitud = solicitud();
        solicitud.setStartDate("2026-02-01");

        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> servicio.consultarMovimientos(solicitud));

        assertEquals(TipoErrorBaneco.VALIDACION, excepcion.getTipo());
    }

    private ConsultaMovimientosSolicitud solicitud() {
        ConsultaMovimientosSolicitud solicitud = new ConsultaMovimientosSolicitud();
        solicitud.setStartDate("2026-01-01");
        solicitud.setEndDate("2026-01-31");
        return solicitud;
    }
}
