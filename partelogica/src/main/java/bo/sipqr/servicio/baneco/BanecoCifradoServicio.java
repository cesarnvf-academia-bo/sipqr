package bo.sipqr.servicio.baneco;

import bo.sipqr.cliente.baneco.BanecoClienteHttp;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class BanecoCifradoServicio {
    private final BanecoClienteHttp clienteHttp;
    private final BanecoConfiguracionServicio configuracionServicio;

    public BanecoCifradoServicio(BanecoClienteHttp clienteHttp,
                                 BanecoConfiguracionServicio configuracionServicio) {
        this.clienteHttp = clienteHttp;
        this.configuracionServicio = configuracionServicio;
    }

    public String encriptar(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, "El texto a encriptar es obligatorio");
        }
        return solicitar("/api/authentication/encrypt", texto.trim());
    }

    public String desencriptar(String textoEncriptado) {
        if (textoEncriptado == null || textoEncriptado.isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, "El texto encriptado es obligatorio");
        }
        return solicitar("/api/authentication/decrypt", textoEncriptado.trim());
    }

    private String solicitar(String ruta, String texto) {
        ConfiguracionBanco configuracion = configuracionServicio.obtener();
        Map<String, String> parametros = new LinkedHashMap<String, String>();
        parametros.put("text", texto);
        parametros.put("aesKey", configuracion.keyencript());
        String respuesta = clienteHttp.obtener(ruta, parametros, null);
        if (respuesta == null || respuesta.isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "El banco devolvió una respuesta vacía al procesar el cifrado");
        }
        return normalizar(respuesta);
    }

    private String normalizar(String respuesta) {
        String valor = respuesta.trim();
        if (valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"")) {
            valor = valor.substring(1, valor.length() - 1);
            valor = valor.replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return valor;
    }
}
