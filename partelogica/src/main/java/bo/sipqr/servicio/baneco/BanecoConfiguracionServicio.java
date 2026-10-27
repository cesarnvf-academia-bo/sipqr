package bo.sipqr.servicio.baneco;

import bo.sipqr.modelo.ConfiguracionQr;
import bo.sipqr.repositorio.ConfiguracionQrRepositorio;
import bo.sipqr.seguridad.ValidadorUrlServicio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BanecoConfiguracionServicio {
    private final ConfiguracionQrRepositorio configuracionQrRepositorio;
    private final ValidadorUrlServicio validadorUrlServicio;

    public BanecoConfiguracionServicio(ConfiguracionQrRepositorio configuracionQrRepositorio,
                                       ValidadorUrlServicio validadorUrlServicio) {
        this.configuracionQrRepositorio = configuracionQrRepositorio;
        this.validadorUrlServicio = validadorUrlServicio;
    }

    @Transactional(readOnly = true)
    public ConfiguracionBanco obtener() {
        ConfiguracionQr configuracion = configuracionQrRepositorio.obtener()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Configuración QR no encontrada"));
        String url = requerido(configuracion.getUrl(), "URL").trim();
        try {
            validadorUrlServicio.validar(url);
        } catch (IllegalArgumentException ex) {
            throw new BanecoExcepcion(TipoErrorBaneco.CONFIGURACION, "La URL bancaria configurada no es válida");
        }
        return new ConfiguracionBanco(
            requerido(configuracion.getUserName(), "usuario").trim(),
            requerido(configuracion.getPassword(), "contraseña"),
            requerido(configuracion.getKeyencript(), "llave AES"),
            requerido(configuracion.getAccount(), "cuenta"),
            url,
            configuracion.getFechaActualizacion()
        );
    }

    private String requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.CONFIGURACION,
                "La configuración bancaria no contiene " + campo);
        }
        return valor;
    }
}
