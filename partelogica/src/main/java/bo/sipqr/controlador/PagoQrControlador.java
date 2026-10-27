package bo.sipqr.controlador;

import bo.sipqr.dto.PaginaRespuesta;
import bo.sipqr.dto.PagoQrRespuesta;
import bo.sipqr.dto.PagoQrSolicitud;
import bo.sipqr.servicio.PagoQrServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagosqr")
@Tag(name = "Pagos QR", description = "Generación, verificación y anulación de pagos QR BANECO")
@SecurityRequirement(name = "bearerAuth")
public class PagoQrControlador {
    private final PagoQrServicio pagoQrServicio;

    public PagoQrControlador(PagoQrServicio pagoQrServicio) {
        this.pagoQrServicio = pagoQrServicio;
    }

    @GetMapping
    @Operation(summary = "Listar pagos QR paginados")
    public ResponseEntity<PaginaRespuesta<PagoQrRespuesta>> encontrarTodos(
            @RequestParam(value = "pagina", defaultValue = "1") int pagina,
            @RequestParam(value = "limite", defaultValue = "5") int limite,
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "estado", required = false) String estado) {
        return ResponseEntity.ok(pagoQrServicio.encontrarTodos(buscar, estado, pagina, limite));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un pago QR")
    public ResponseEntity<PagoQrRespuesta> encontrarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pagoQrServicio.encontrarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar y generar un QR BANECO")
    public ResponseEntity<PagoQrRespuesta> generar(@Valid @RequestBody PagoQrSolicitud solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoQrServicio.generar(solicitud));
    }

    @PostMapping("/{id}/verificar")
    @Operation(summary = "Verificar el estado del QR y conciliar sus pagos")
    public ResponseEntity<PagoQrRespuesta> verificar(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pagoQrServicio.verificar(id));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular un QR pendiente")
    public ResponseEntity<PagoQrRespuesta> anular(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pagoQrServicio.anular(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar un pago QR registrado sin QR generado")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id) {
        pagoQrServicio.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
