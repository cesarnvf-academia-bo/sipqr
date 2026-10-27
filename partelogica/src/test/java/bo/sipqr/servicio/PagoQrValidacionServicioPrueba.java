package bo.sipqr.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.BanecoValidacionServicio;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PagoQrValidacionServicioPrueba {
    private PagoQrValidacionServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new PagoQrValidacionServicio(new BanecoValidacionServicio());
    }

    @Test
    void aceptaPngEnBase64SinPrefijo() {
        String imagen = pngBase64();

        assertEquals(imagen, servicio.limpiarImagenQr(imagen));
    }

    @Test
    void aceptaPngConPrefijoDataUri() {
        String imagen = pngBase64();

        assertEquals(imagen, servicio.limpiarImagenQr("data:image/png;base64," + imagen));
    }

    @Test
    void rechazaContenidoQueNoEsPng() {
        String imagen = Base64.getEncoder().encodeToString("no-es-png".getBytes());

        assertThrows(BanecoExcepcion.class, () -> servicio.limpiarImagenQr(imagen));
    }

    @Test
    void rechazaDataUriQueNoEsPng() {
        String imagen = Base64.getEncoder().encodeToString("texto".getBytes());

        assertThrows(BanecoExcepcion.class,
            () -> servicio.limpiarImagenQr("data:text/plain;base64," + imagen));
    }

    private String pngBase64() {
        byte[] firma = {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
        };
        return Base64.getEncoder().encodeToString(firma);
    }
}
