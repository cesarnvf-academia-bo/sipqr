package bo.sipqr.repositorio.impl;

import bo.sipqr.modelo.ConfiguracionQr;
import bo.sipqr.repositorio.ConfiguracionQrRepositorio;
import bo.sipqr.repositorio.mapeador.ConfiguracionQrMapeador;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ConfiguracionQrRepositorioImpl implements ConfiguracionQrRepositorio {
    private final ConfiguracionQrMapeador mapeador;

    public ConfiguracionQrRepositorioImpl(ConfiguracionQrMapeador mapeador) {
        this.mapeador = mapeador;
    }

    @Override
    public Optional<ConfiguracionQr> obtener() {
        return Optional.ofNullable(mapeador.encontrarConfiguracion());
    }

    @Override
    public void actualizar(ConfiguracionQr configuracion) {
        mapeador.actualizarConfiguracion(configuracion);
    }
}
