package bo.sipqr.cliente.baneco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bo.sipqr.seguridad.baneco.TokenBanecoServicio;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BanecoClienteAutenticadoPrueba {
    private BanecoClienteHttp cliente;
    private TokenBanecoServicio token;
    private BanecoClienteAutenticado autenticado;

    @BeforeEach
    void preparar() {
        cliente = mock(BanecoClienteHttp.class);
        token = mock(TokenBanecoServicio.class);
        autenticado = new BanecoClienteAutenticado(cliente, token);
    }

    @Test
    void renuevaTokenYReintentaUnaVezAnteNoAutorizado() {
        when(token.obtenerToken()).thenReturn("token-1", "token-2");
        when(cliente.postJson(eq("/api/qrsimple/generateQR"), any(), eq("token-1")))
            .thenThrow(new BanecoExcepcion(TipoErrorBaneco.COMUNICACION, "No autorizado", null, 401));
        when(cliente.postJson(eq("/api/qrsimple/generateQR"), any(), eq("token-2")))
            .thenReturn("{\"responseCode\":0}");

        String respuesta = autenticado.postJson("/api/qrsimple/generateQR", new Object());

        assertEquals("{\"responseCode\":0}", respuesta);
        verify(token).invalidar();
        verify(cliente, times(2)).postJson(eq("/api/qrsimple/generateQR"), any(), any());
    }
}
