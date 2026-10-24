package bo.sipqr.controlador;

import bo.sipqr.dto.PaginaRespuesta;
import bo.sipqr.dto.UsuarioActualizacion;
import bo.sipqr.dto.UsuarioRespuesta;
import bo.sipqr.dto.UsuarioSolicitud;
import bo.sipqr.servicio.UsuarioServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestion de usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioControlador {
    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios paginados con busqueda")
    public ResponseEntity<PaginaRespuesta<UsuarioRespuesta>> encontrarTodos(
            @RequestParam(value = "pagina", defaultValue = "1") int pagina,
            @RequestParam(value = "limite", defaultValue = "5") int limite,
            @RequestParam(value = "buscar", required = false) String buscar) {
        PaginaRespuesta<UsuarioRespuesta> respuesta = usuarioServicio.encontrarTodos(buscar, pagina, limite);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Encontrar usuario por id")
    public ResponseEntity<UsuarioRespuesta> encontrarPorId(@PathVariable("id") Long id) {
        Optional<UsuarioRespuesta> respuesta = usuarioServicio.encontrarPorId(id);
        return respuesta.map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @PostMapping
    @Operation(summary = "Adicionar usuario")
    public ResponseEntity<UsuarioRespuesta> adicionar(@Valid @RequestBody UsuarioSolicitud solicitud) {
        UsuarioRespuesta respuesta = usuarioServicio.adicionar(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UsuarioRespuesta> actualizar(@PathVariable("id") Long id, @Valid @RequestBody UsuarioActualizacion actualizacion) {
        UsuarioRespuesta respuesta = usuarioServicio.actualizar(id, actualizacion);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar usuario")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id) {
        usuarioServicio.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
