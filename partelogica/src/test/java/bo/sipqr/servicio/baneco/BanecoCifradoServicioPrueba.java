package bo.sipqr.servicio.baneco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bo.sipqr.cliente.baneco.BanecoClienteHttp;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class BanecoCifradoServicioPrueba {
    private BanecoClienteHttp cliente;
    private BanecoCifradoServicio servicio;

    @BeforeEach
    void preparar() {
        cliente = mock(BanecoClienteHttp.class);
        BanecoConfiguracionServicio configuracion = mock(BanecoConfiguracionServicio.class);
        when(configuracion.obtener()).thenReturn(new ConfiguracionBanco(
            "usuario", "clave", "llave", "cuenta", "https://apimktdesa.baneco.com.bo", OffsetDateTime.now()));
        servicio = new BanecoCifradoServicio(cliente, configuracion);
    }

    @Test
    void enviaTextoYQuitaComillasDeRespuesta() {
        when(cliente.obtener(eq("/api/authentication/encrypt"), any(), eq(null)))
            .thenReturn("\"TEXTO-CIFRADO\"");

        String respuesta = servicio.encriptar("hola");

        assertEquals("TEXTO-CIFRADO", respuesta);
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        verify(cliente).obtener(eq("/api/authentication/encrypt"), captor.capture(), eq(null));
        assertEquals("hola", captor.getValue().get("text"));
        assertEquals("llave", captor.getValue().get("aesKey"));
    }

    @Test
    void enviaTextoEncriptadoYQuitaComillasDeRespuesta() {
        when(cliente.obtener(eq("/api/authentication/decrypt"), any(), eq(null)))
            .thenReturn("\"1234\"");

        assertEquals("1234", servicio.desencriptar("TEXTO-CIFRADO"));
    }

    @Test
    void rechazaTextoVacio() {
        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class, () -> servicio.encriptar(" "));
        assertEquals(TipoErrorBaneco.VALIDACION, excepcion.getTipo());
    }
}
