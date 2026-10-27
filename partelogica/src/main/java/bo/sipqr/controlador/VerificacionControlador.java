package bo.sipqr.controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verificacion")
@Tag(name = "Verificación", description = "Estado de disponibilidad del servicio")
public class VerificacionControlador {
    @GetMapping("/estado")
    @Operation(summary = "Verificar disponibilidad")
    public ResponseEntity<Map<String, Object>> verificarEstado() {
        Map<String, Object> respuesta = new LinkedHashMap<String, Object>();
        respuesta.put("estado", "OK");
        respuesta.put("mensaje", "Servicio SIPQR activo");
        respuesta.put("fecha", OffsetDateTime.now());
        return ResponseEntity.ok(respuesta);
    }
}
