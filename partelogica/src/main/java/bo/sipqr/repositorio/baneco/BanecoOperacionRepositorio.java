package bo.sipqr.repositorio.baneco;

import bo.sipqr.modelo.baneco.BanecoDetalleLote;
import bo.sipqr.modelo.baneco.BanecoOperacion;
import java.util.Optional;

public interface BanecoOperacionRepositorio {
    Optional<BanecoOperacion> buscarOperacion(String tipo, String transactionId);

    void insertarOperacion(BanecoOperacion operacion);

    void actualizarOperacion(BanecoOperacion operacion);

    boolean registrarNotificacion(String tipo, String claveEvento, String hashPayload, String estado);

    void guardarDetalleLote(BanecoDetalleLote detalle);
}
