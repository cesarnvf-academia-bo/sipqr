package bo.sipqr.servicio;

import bo.sipqr.dto.PaginaRespuesta;
import bo.sipqr.dto.PagoQrRespuesta;
import bo.sipqr.dto.PagoQrSolicitud;

public interface PagoQrServicio {
    PaginaRespuesta<PagoQrRespuesta> encontrarTodos(String buscar, String estado, int pagina, int limite);

    PagoQrRespuesta encontrarPorId(Long id);

    PagoQrRespuesta generar(PagoQrSolicitud solicitud);

    PagoQrRespuesta verificar(Long id);

    PagoQrRespuesta anular(Long id);

    void borrar(Long id);
}
