package bo.sipqr.servicio;

import bo.sipqr.dto.ConfiguracionQrRespuesta;
import bo.sipqr.dto.ConfiguracionQrSolicitud;

public interface ConfiguracionQrServicio {
    ConfiguracionQrRespuesta obtener();

    ConfiguracionQrRespuesta actualizar(ConfiguracionQrSolicitud solicitud);
}
