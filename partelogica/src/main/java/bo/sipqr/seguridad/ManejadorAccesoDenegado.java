package bo.sipqr.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class ManejadorAccesoDenegado implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest solicitud, HttpServletResponse respuesta, AccessDeniedException excepcion)
            throws IOException {
        respuesta.setStatus(HttpServletResponse.SC_FORBIDDEN);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.getWriter().write("{\"mensaje\":\"Acceso denegado\"}");
    }
}
