package bo.sipqr.servicio.impl;

import bo.sipqr.dto.AccesoRespuesta;
import bo.sipqr.dto.AccesoSolicitud;
import bo.sipqr.modelo.Usuario;
import bo.sipqr.repositorio.UsuarioRepositorio;
import bo.sipqr.seguridad.JwtServicio;
import bo.sipqr.servicio.AutenticacionServicio;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AutenticacionServicioImpl implements AutenticacionServicio {
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder codificadorContrasena;
    private final JwtServicio jwtServicio;
    private final Set<String> administradores;

    public AutenticacionServicioImpl(UsuarioRepositorio usuarioRepositorio,
                                     PasswordEncoder codificadorContrasena,
                                     JwtServicio jwtServicio,
                                     @Value("${seguridad.usuarios-administradores:administrador}") String administradores) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.codificadorContrasena = codificadorContrasena;
        this.jwtServicio = jwtServicio;
        this.administradores = Arrays.stream(administradores.split(","))
            .map(String::trim)
            .filter(valor -> !valor.isBlank())
            .map(valor -> valor.toLowerCase(java.util.Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public AccesoRespuesta autenticar(AccesoSolicitud solicitud) {
        Usuario usuario = usuarioRepositorio.encontrarPorUsuario(solicitud.getUsuario().trim())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!Boolean.TRUE.equals(usuario.getEstado())
                || !codificadorContrasena.matches(solicitud.getClave(), usuario.getClave())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        String rol = administradores.contains(usuario.getUsuario().toLowerCase(java.util.Locale.ROOT)) ? "ADMIN" : "USUARIO";
        String token = jwtServicio.generarToken(usuario.getUsuario(), rol);
        return new AccesoRespuesta(token, "Bearer", usuario.getUsuario(), rol, jwtServicio.getExpiracionMs() / 1000);
    }
}
