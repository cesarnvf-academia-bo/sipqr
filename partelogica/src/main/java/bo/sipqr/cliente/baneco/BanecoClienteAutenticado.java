package bo.sipqr.cliente.baneco;

import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import bo.sipqr.seguridad.baneco.TokenBanecoServicio;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class BanecoClienteAutenticado {
    private final BanecoClienteHttp clienteHttp;
    private final TokenBanecoServicio tokenServicio;

    public BanecoClienteAutenticado(BanecoClienteHttp clienteHttp, TokenBanecoServicio tokenServicio) {
        this.clienteHttp = clienteHttp;
        this.tokenServicio = tokenServicio;
    }

    public String obtener(String ruta, Map<String, String> parametros) {
        return conRenovacion(token -> clienteHttp.obtener(ruta, parametros, token));
    }

    public String postJson(String ruta, Object cuerpo) {
        return conRenovacion(token -> clienteHttp.postJson(ruta, cuerpo, token));
    }

    public String deleteJson(String ruta, Object cuerpo) {
        return conRenovacion(token -> clienteHttp.deleteJson(ruta, cuerpo, token));
    }

    private String conRenovacion(Function<String, String> operacion) {
        try {
            return operacion.apply(tokenServicio.obtenerToken());
        } catch (BanecoExcepcion ex) {
            if (ex.getEstadoHttp() != null && (ex.getEstadoHttp() == 401 || ex.getEstadoHttp() == 403)) {
                tokenServicio.invalidar();
                return operacion.apply(tokenServicio.obtenerToken());
            }
            throw ex;
        }
    }
}
