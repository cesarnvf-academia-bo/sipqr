package bo.sipqr.servicio.baneco;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

class BanecoWebhookValidadorPrueba {
    @Test
    void aceptaSecretoCorrecto() {
        BanecoWebhookValidador validador = new BanecoWebhookValidador("secreto-compartido");
        HttpServletRequest solicitud = mock(HttpServletRequest.class);
        when(solicitud.getHeader(BanecoWebhookValidador.CABECERA_SECRETO)).thenReturn("secreto-compartido");

        assertDoesNotThrow(() -> validador.validar(solicitud));
    }

    @Test
    void rechazaSecretoIncorrecto() {
        BanecoWebhookValidador validador = new BanecoWebhookValidador("secreto-compartido");
        HttpServletRequest solicitud = mock(HttpServletRequest.class);
        when(solicitud.getHeader(BanecoWebhookValidador.CABECERA_SECRETO)).thenReturn("otro");

        assertThrows(BanecoExcepcion.class, () -> validador.validar(solicitud));
    }

    @Test
    void fallaCerradoSinSecretoConfigurado() {
        BanecoWebhookValidador validador = new BanecoWebhookValidador("");
        HttpServletRequest solicitud = mock(HttpServletRequest.class);

        assertThrows(BanecoExcepcion.class, () -> validador.validar(solicitud));
    }
}
