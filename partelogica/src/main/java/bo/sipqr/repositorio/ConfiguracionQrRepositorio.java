package bo.sipqr.repositorio;

import bo.sipqr.modelo.ConfiguracionQr;
import java.util.Optional;

public interface ConfiguracionQrRepositorio {
    Optional<ConfiguracionQr> obtener();

    void actualizar(ConfiguracionQr configuracion);
}
