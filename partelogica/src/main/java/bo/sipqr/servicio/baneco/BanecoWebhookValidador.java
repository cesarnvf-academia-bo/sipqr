package bo.sipqr.servicio.baneco;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BanecoWebhookValidador {
    public static final String CABECERA_SECRETO = "X-SIPQR-Secreto";

    private final String secreto;

    public BanecoWebhookValidador(@Value("${baneco.webhook.secreto:}") String secreto) {
        this.secreto = secreto;
    }

    public void validar(HttpServletRequest solicitud) {
        if (secreto == null || secreto.isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.WEBHOOK,
                "El receptor de notificaciones bancarias no está configurado", null, 503);
        }
        String recibido = solicitud.getHeader(CABECERA_SECRETO);
        if (recibido == null || !compararSeguro(secreto, recibido)) {
            throw new BanecoExcepcion(TipoErrorBaneco.WEBHOOK,
                "Notificación bancaria no autorizada", null, 401);
        }
    }

    private boolean compararSeguro(String esperado, String recibido) {
        byte[] a = esperado.getBytes(StandardCharsets.UTF_8);
        byte[] b = recibido.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }
}
