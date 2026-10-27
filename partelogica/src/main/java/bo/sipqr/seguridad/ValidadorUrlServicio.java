package bo.sipqr.seguridad;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class ValidadorUrlServicio {
    public void validar(String url) {
        URI uri;
        try {
            uri = new URI(url.trim());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("La URL no tiene un formato válido");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("La URL debe utilizar HTTPS");
        }
        if (uri.getUserInfo() != null || uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalArgumentException("La URL debe incluir un host válido y no credenciales embebidas");
        }
        if (uri.getFragment() != null) {
            throw new IllegalArgumentException("La URL no debe contener fragmentos");
        }
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        if (host.equals("localhost") || host.endsWith(".localhost") || host.endsWith(".local")
                || host.endsWith(".internal") || host.endsWith(".home.arpa")) {
            throw new IllegalArgumentException("La URL no puede apuntar a un host interno");
        }
        if (esIpPrivada(host)) {
            throw new IllegalArgumentException("La URL no puede apuntar a una dirección IP privada o reservada");
        }
        if (uri.getPort() != -1 && (uri.getPort() < 1 || uri.getPort() > 65535)) {
            throw new IllegalArgumentException("El puerto de la URL no es válido");
        }
    }

    private boolean esIpPrivada(String host) {
        if (host.startsWith("10.") || host.startsWith("127.") || host.startsWith("169.254.")
                || host.startsWith("192.168.") || host.startsWith("0.")) {
            return true;
        }
        if (host.startsWith("172.")) {
            String[] partes = host.split("\\.");
            if (partes.length == 4) {
                try {
                    int segundo = Integer.parseInt(partes[1]);
                    return segundo >= 16 && segundo <= 31;
                } catch (NumberFormatException ignored) {
                    return true;
                }
            }
        }
        if (host.startsWith("[") || host.contains(":")) {
            String limpio = host.replace("[", "").replace("]", "");
            return limpio.equals("::1") || limpio.startsWith("fc") || limpio.startsWith("fd")
                    || limpio.startsWith("fe80:");
        }
        return false;
    }
}
