package bo.sipqr.servicio.baneco;

import bo.sipqr.modelo.baneco.RespuestaBaneco;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class BanecoRespuestaServicio {
    private final ObjectMapper objectMapper;

    public BanecoRespuestaServicio(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serializar(Object valor) {
        try {
            return objectMapper.writeValueAsString(valor);
        } catch (Exception ex) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "No fue posible preparar la solicitud bancaria", ex);
        }
    }

    public <T> T leer(String json, Class<T> tipo) {
        if (json == null || json.isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA, "El banco devolvió una respuesta vacía");
        }
        try {
            return objectMapper.readValue(json, tipo);
        } catch (Exception ex) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "El banco devolvió una respuesta con formato inválido", ex);
        }
    }

    public void validar(RespuestaBaneco respuesta) {
        if (respuesta == null || respuesta.getResponseCode() == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "La respuesta bancaria no contiene el código de respuesta");
        }
        if (respuesta.getResponseCode() != 0) {
            String mensaje = respuesta.getMessage() == null || respuesta.getMessage().isBlank()
                ? "La operación bancaria fue rechazada"
                : respuesta.getMessage();
            throw new BanecoExcepcion(TipoErrorBaneco.OPERACION_RECHAZADA, mensaje,
                respuesta.getResponseCode(), null);
        }
    }

    public <T extends RespuestaBaneco> T leerYValidar(String json, Class<T> tipo) {
        T respuesta = leer(json, tipo);
        validar(respuesta);
        return respuesta;
    }

    public String hash(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder salida = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                salida.append(String.format("%02x", b));
            }
            return salida.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible", ex);
        }
    }
}
