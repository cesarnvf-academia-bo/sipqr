package bo.sipqr.seguridad;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FiltroAutenticacionJwt extends OncePerRequestFilter {
    private final JwtServicio jwtServicio;

    public FiltroAutenticacionJwt(JwtServicio jwtServicio) {
        this.jwtServicio = jwtServicio;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest solicitud, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        String cabecera = solicitud.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            String token = cabecera.substring(7).trim();
            jwtServicio.validarToken(token).ifPresent(claims -> autenticar(solicitud, claims));
        }
        cadena.doFilter(solicitud, respuesta);
    }

    private void autenticar(HttpServletRequest solicitud, Claims claims) {
        String usuario = claims.getSubject();
        String rol = claims.get("rol", String.class);
        if (usuario == null || rol == null || rol.isBlank()) {
            return;
        }
        UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(
            usuario,
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + rol))
        );
        autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(solicitud));
        SecurityContextHolder.getContext().setAuthentication(autenticacion);
    }
}
