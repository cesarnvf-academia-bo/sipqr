package bo.sipqr.servicio.baneco;

import bo.sipqr.dto.baneco.EstadoConexionBaneco;
import bo.sipqr.seguridad.baneco.TokenBanecoServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BanecoAutenticacionServicio {
    private static final Logger REGISTRO = LoggerFactory.getLogger(BanecoAutenticacionServicio.class);

    private final TokenBanecoServicio tokenServicio;

    public BanecoAutenticacionServicio(TokenBanecoServicio tokenServicio) {
        this.tokenServicio = tokenServicio;
    }

    public EstadoConexionBaneco verificarConexion() {
        try {
            tokenServicio.obtenerToken();
            return new EstadoConexionBaneco(true, "Conexión bancaria autenticada correctamente");
        } catch (BanecoExcepcion ex) {
            REGISTRO.warn("BANECO autenticación fallida: {}", ex.getTipo());
            return new EstadoConexionBaneco(false, "No fue posible autenticar con el banco");
        }
    }
}
