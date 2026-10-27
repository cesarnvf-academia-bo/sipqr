package bo.sipqr.seguridad.baneco;

import bo.sipqr.cliente.baneco.BanecoClienteHttp;
import bo.sipqr.modelo.baneco.RespuestaAutenticacionBaneco;
import bo.sipqr.servicio.baneco.BanecoCifradoServicio;
import bo.sipqr.servicio.baneco.BanecoConfiguracionServicio;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.BanecoRespuestaServicio;
import bo.sipqr.servicio.baneco.ConfiguracionBanco;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class TokenBanecoServicio {
    private static final Logger REGISTRO = LoggerFactory.getLogger(TokenBanecoServicio.class);

    private final BanecoClienteHttp clienteHttp;
    private final BanecoCifradoServicio cifradoServicio;
    private final BanecoConfiguracionServicio configuracionServicio;
    private final BanecoRespuestaServicio respuestaServicio;
    private final ObjectMapper objectMapper;
    private volatile TokenCacheado cache;

    public TokenBanecoServicio(BanecoClienteHttp clienteHttp,
                               BanecoCifradoServicio cifradoServicio,
                               BanecoConfiguracionServicio configuracionServicio,
                               BanecoRespuestaServicio respuestaServicio,
                               ObjectMapper objectMapper) {
        this.clienteHttp = clienteHttp;
        this.cifradoServicio = cifradoServicio;
        this.configuracionServicio = configuracionServicio;
        this.respuestaServicio = respuestaServicio;
        this.objectMapper = objectMapper;
    }

    public synchronized String obtenerToken() {
        ConfiguracionBanco configuracion = configuracionServicio.obtener();
        TokenCacheado actual = cache;
        if (actual != null
                && Objects.equals(actual.fechaConfiguracion, configuracion.fechaActualizacion())
                && !actual.vencido()) {
            return actual.token;
        }
        REGISTRO.info("BANECO autenticación iniciada");
        String token = autenticar(configuracion);
        Instant expiracion = obtenerExpiracion(token);
        cache = new TokenCacheado(token, configuracion.fechaActualizacion(), expiracion);
        REGISTRO.info("BANECO autenticación completada");
        return token;
    }

    public synchronized void invalidar() {
        cache = null;
    }

    private String autenticar(ConfiguracionBanco configuracion) {
        String passwordCifrado = cifradoServicio.encriptar(configuracion.password());
        Map<String, String> solicitud = new LinkedHashMap<String, String>();
        solicitud.put("userName", configuracion.userName());
        solicitud.put("password", passwordCifrado);
        String json = clienteHttp.postJson("/api/authentication/authenticate", solicitud, null);
        RespuestaAutenticacionBaneco respuesta = respuestaServicio.leer(json, RespuestaAutenticacionBaneco.class);
        respuestaServicio.validar(respuesta);
        if (respuesta.getToken() == null || respuesta.getToken().isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.AUTENTICACION,
                "El banco no devolvió un token de autenticación");
        }
        return respuesta.getToken();
    }

    private Instant obtenerExpiracion(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length < 2) {
                return null;
            }
            byte[] carga = Base64.getUrlDecoder().decode(partes[1]);
            JsonNode nodo = objectMapper.readTree(new String(carga, StandardCharsets.UTF_8));
            if (nodo.has("exp") && nodo.get("exp").isNumber()) {
                return Instant.ofEpochSecond(nodo.get("exp").asLong());
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    private static final class TokenCacheado {
        private final String token;
        private final java.time.OffsetDateTime fechaConfiguracion;
        private final Instant expiracion;

        private TokenCacheado(String token, java.time.OffsetDateTime fechaConfiguracion, Instant expiracion) {
            this.token = token;
            this.fechaConfiguracion = fechaConfiguracion;
            this.expiracion = expiracion;
        }

        private boolean vencido() {
            return expiracion != null && Instant.now().isAfter(expiracion.minusSeconds(60));
        }
    }
}
