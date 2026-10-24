package bo.sipqr.repositorio;

import bo.sipqr.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio {
    List<Usuario> encontrarTodos(String buscar, int limite, int desplazamiento);
    Optional<Usuario> encontrarPorId(Long id);
    Optional<Usuario> encontrarPorUsuario(String usuario);
    long contar(String buscar);
    int adicionar(Usuario usuario);
    int actualizar(Usuario usuario);
    int borrar(Long id);
}
