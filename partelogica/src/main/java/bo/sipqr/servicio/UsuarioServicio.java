package bo.sipqr.servicio;

import bo.sipqr.dto.PaginaRespuesta;
import bo.sipqr.dto.UsuarioActualizacion;
import bo.sipqr.dto.UsuarioRespuesta;
import bo.sipqr.dto.UsuarioSolicitud;
import java.util.Optional;

public interface UsuarioServicio {
    PaginaRespuesta<UsuarioRespuesta> encontrarTodos(String buscar, int pagina, int limite);
    Optional<UsuarioRespuesta> encontrarPorId(Long id);
    UsuarioRespuesta adicionar(UsuarioSolicitud solicitud);
    UsuarioRespuesta actualizar(Long id, UsuarioActualizacion actualizacion);
    boolean borrar(Long id);
}
