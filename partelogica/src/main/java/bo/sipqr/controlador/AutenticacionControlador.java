package bo.sipqr.controlador;

import bo.sipqr.dto.AccesoRespuesta;
import bo.sipqr.dto.AccesoSolicitud;
import bo.sipqr.servicio.AutenticacionServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/acceso")
@Tag(name = "Acceso", description = "Autenticación de usuarios")
public class AutenticacionControlador {
    private final AutenticacionServicio autenticacionServicio;

    public AutenticacionControlador(AutenticacionServicio autenticacionServicio) {
        this.autenticacionServicio = autenticacionServicio;
    }

    @PostMapping
    @Operation(summary = "Iniciar sesión", description = "Devuelve un token JWT para usuarios activos")
    public ResponseEntity<AccesoRespuesta> iniciarSesion(@Valid @RequestBody AccesoSolicitud solicitud) {
        return ResponseEntity.ok(autenticacionServicio.autenticar(solicitud));
    }
}
