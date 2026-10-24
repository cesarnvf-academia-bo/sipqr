package bo.sipqr.repositorio.mapeador;

import bo.sipqr.modelo.Usuario;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UsuarioMapeador {
    List<Usuario> encontrarTodos(@Param("buscar") String buscar, @Param("limite") int limite, @Param("desplazamiento") int desplazamiento);
    Usuario encontrarPorId(@Param("id") Long id);
    Usuario encontrarPorUsuario(@Param("usuario") String usuario);
    long contar(@Param("buscar") String buscar);
    int adicionar(Usuario usuario);
    int actualizar(Usuario usuario);
    int borrar(@Param("id") Long id);
}
