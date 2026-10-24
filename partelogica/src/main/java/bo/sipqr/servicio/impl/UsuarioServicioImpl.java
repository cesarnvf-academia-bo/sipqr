package bo.sipqr.servicio.impl;

import bo.sipqr.dto.PaginaRespuesta;
import bo.sipqr.dto.UsuarioActualizacion;
import bo.sipqr.dto.UsuarioRespuesta;
import bo.sipqr.dto.UsuarioSolicitud;
import bo.sipqr.modelo.Usuario;
import bo.sipqr.repositorio.UsuarioRepositorio;
import bo.sipqr.servicio.UsuarioServicio;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder codificadorContrasena;

    public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio, PasswordEncoder codificadorContrasena) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.codificadorContrasena = codificadorContrasena;
    }

    @Override
    public PaginaRespuesta<UsuarioRespuesta> encontrarTodos(String buscar, int pagina, int limite) {
        int paginaValida = pagina < 1 ? 1 : pagina;
        int limiteValido = (limite == 5 || limite == 10 || limite == 20) ? limite : 5;
        int desplazamiento = (paginaValida - 1) * limiteValido;
        String filtro = (buscar == null || buscar.isBlank()) ? null : buscar.trim();
        List<Usuario> usuarios = usuarioRepositorio.encontrarTodos(filtro, limiteValido, desplazamiento);
        long total = usuarioRepositorio.contar(filtro);
        List<UsuarioRespuesta> datos = usuarios.stream()
            .map(usuario -> new UsuarioRespuesta(usuario.getId(), usuario.getUsuario(), usuario.getEstado()))
            .collect(Collectors.toList());
        return new PaginaRespuesta<UsuarioRespuesta>(datos, paginaValida, limiteValido, total);
    }

    @Override
    public Optional<UsuarioRespuesta> encontrarPorId(Long id) {
        return usuarioRepositorio.encontrarPorId(id)
            .map(usuario -> new UsuarioRespuesta(usuario.getId(), usuario.getUsuario(), usuario.getEstado()));
    }

    @Override
    @Transactional
    public UsuarioRespuesta adicionar(UsuarioSolicitud solicitud) {
        String nombreUsuario = solicitud.getUsuario() == null ? null : solicitud.getUsuario().trim();
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }
        if (nombreUsuario.length() < 4 || nombreUsuario.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe tener entre 4 y 100 caracteres");
        }
        if (solicitud.getClave() == null || solicitud.getClave().length() < 12 || solicitud.getClave().length() > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave debe tener entre 12 y 72 caracteres");
        }
        if (solicitud.getEstado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio");
        }
        Optional<Usuario> existente = usuarioRepositorio.encontrarPorUsuario(nombreUsuario);
        if (existente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setUsuario(nombreUsuario);
        usuario.setClave(codificadorContrasena.encode(solicitud.getClave()));
        usuario.setEstado(solicitud.getEstado());
        usuarioRepositorio.adicionar(usuario);
        return new UsuarioRespuesta(usuario.getId(), usuario.getUsuario(), usuario.getEstado());
    }

    @Override
    @Transactional
    public UsuarioRespuesta actualizar(Long id, UsuarioActualizacion actualizacion) {
        Usuario usuarioExistente = usuarioRepositorio.encontrarPorId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        String nombreUsuario = actualizacion.getUsuario() == null ? null : actualizacion.getUsuario().trim();
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }
        if (nombreUsuario.length() < 4 || nombreUsuario.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe tener entre 4 y 100 caracteres");
        }
        if (actualizacion.getEstado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio");
        }
        Optional<Usuario> duplicado = usuarioRepositorio.encontrarPorUsuario(nombreUsuario);
        if (duplicado.isPresent() && !duplicado.get().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya está registrado");
        }
        usuarioExistente.setUsuario(nombreUsuario);
        usuarioExistente.setEstado(actualizacion.getEstado());
        if (actualizacion.getClave() != null && !actualizacion.getClave().isBlank()) {
            if (actualizacion.getClave().length() < 12 || actualizacion.getClave().length() > 72) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave debe tener entre 12 y 72 caracteres");
            }
            usuarioExistente.setClave(codificadorContrasena.encode(actualizacion.getClave()));
        }
        usuarioRepositorio.actualizar(usuarioExistente);
        return new UsuarioRespuesta(usuarioExistente.getId(), usuarioExistente.getUsuario(), usuarioExistente.getEstado());
    }

    @Override
    @Transactional
    public boolean borrar(Long id) {
        Usuario usuarioExistente = usuarioRepositorio.encontrarPorId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if ("administrador".equalsIgnoreCase(usuarioExistente.getUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No es posible eliminar el usuario administrador");
        }
        return usuarioRepositorio.borrar(id) > 0;
    }
}
