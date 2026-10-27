package bo.sipqr.controlador;

import bo.sipqr.dto.ConfiguracionQrRespuesta;
import bo.sipqr.dto.ConfiguracionQrSolicitud;
import bo.sipqr.servicio.ConfiguracionQrServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracionqr")
@Tag(name = "Configuración QR", description = "Administración de la configuración bancaria para pagos QR")
@SecurityRequirement(name = "bearerAuth")
public class ConfiguracionQrControlador {
    private final ConfiguracionQrServicio configuracionQrServicio;

    public ConfiguracionQrControlador(ConfiguracionQrServicio configuracionQrServicio) {
        this.configuracionQrServicio = configuracionQrServicio;
    }

    @GetMapping
    @Operation(summary = "Obtener configuración QR", description = "Devuelve la configuración sin revelar los secretos almacenados")
    public ResponseEntity<ConfiguracionQrRespuesta> obtener() {
        return ResponseEntity.ok(configuracionQrServicio.obtener());
    }

    @PutMapping
    @Operation(summary = "Actualizar configuración QR", description = "Conserva los secretos omitidos y cifra los nuevos valores")
    public ResponseEntity<ConfiguracionQrRespuesta> actualizar(@Valid @RequestBody ConfiguracionQrSolicitud solicitud) {
        return ResponseEntity.ok(configuracionQrServicio.actualizar(solicitud));
    }
}
