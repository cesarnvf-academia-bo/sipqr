package bo.sipqr.repositorio.impl;

import bo.sipqr.modelo.Usuario;
import bo.sipqr.repositorio.UsuarioRepositorio;
import bo.sipqr.repositorio.mapeador.UsuarioMapeador;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepositorioImpl implements UsuarioRepositorio {
    private final UsuarioMapeador usuarioMapeador;

    public UsuarioRepositorioImpl(UsuarioMapeador usuarioMapeador) {
        this.usuarioMapeador = usuarioMapeador;
    }

    @Override
    public List<Usuario> encontrarTodos(String buscar, int limite, int desplazamiento) {
        return usuarioMapeador.encontrarTodos(buscar, limite, desplazamiento);
    }

    @Override
    public Optional<Usuario> encontrarPorId(Long id) {
        return Optional.ofNullable(usuarioMapeador.encontrarPorId(id));
    }

    @Override
    public Optional<Usuario> encontrarPorUsuario(String usuario) {
        return Optional.ofNullable(usuarioMapeador.encontrarPorUsuario(usuario));
    }

    @Override
    public long contar(String buscar) {
        return usuarioMapeador.contar(buscar);
    }

    @Override
    public int adicionar(Usuario usuario) {
        return usuarioMapeador.adicionar(usuario);
    }

    @Override
    public int actualizar(Usuario usuario) {
        return usuarioMapeador.actualizar(usuario);
    }

    @Override
    public int borrar(Long id) {
        return usuarioMapeador.borrar(id);
    }
}
