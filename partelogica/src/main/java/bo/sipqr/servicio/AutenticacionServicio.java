package bo.sipqr.servicio;

import bo.sipqr.dto.AccesoRespuesta;
import bo.sipqr.dto.AccesoSolicitud;

public interface AutenticacionServicio {
    AccesoRespuesta autenticar(AccesoSolicitud solicitud);
}
