package bo.sipqr.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class ManejadorNoAutorizado implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest solicitud, HttpServletResponse respuesta, AuthenticationException excepcion)
            throws IOException {
        respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.getWriter().write("{\"mensaje\":\"No autorizado\"}");
    }
}
