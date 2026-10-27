package bo.sipqr.repositorio.mapeador;

import bo.sipqr.modelo.ConfiguracionQr;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfiguracionQrMapeador {
    ConfiguracionQr encontrarConfiguracion();

    int actualizarConfiguracion(ConfiguracionQr configuracion);
}
