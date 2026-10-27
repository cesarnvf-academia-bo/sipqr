package bo.sipqr.repositorio.impl.baneco;

import bo.sipqr.modelo.baneco.BanecoDetalleLote;
import bo.sipqr.modelo.baneco.BanecoOperacion;
import bo.sipqr.repositorio.baneco.BanecoOperacionRepositorio;
import bo.sipqr.repositorio.mapeador.baneco.BanecoOperacionMapeador;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
public class BanecoOperacionRepositorioImpl implements BanecoOperacionRepositorio {
    private final BanecoOperacionMapeador mapeador;

    public BanecoOperacionRepositorioImpl(BanecoOperacionMapeador mapeador) {
        this.mapeador = mapeador;
    }

    @Override
    public Optional<BanecoOperacion> buscarOperacion(String tipo, String transactionId) {
        return Optional.ofNullable(mapeador.buscarOperacion(tipo, transactionId));
    }

    @Override
    public void insertarOperacion(BanecoOperacion operacion) {
        mapeador.insertarOperacion(operacion);
    }

    @Override
    public void actualizarOperacion(BanecoOperacion operacion) {
        mapeador.actualizarOperacion(operacion);
    }

    @Override
    public boolean registrarNotificacion(String tipo, String claveEvento, String hashPayload, String estado) {
        try {
            return mapeador.insertarNotificacion(tipo, claveEvento, hashPayload, estado) > 0;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

    @Override
    public void guardarDetalleLote(BanecoDetalleLote detalle) {
        mapeador.guardarDetalleLote(detalle);
    }
}
