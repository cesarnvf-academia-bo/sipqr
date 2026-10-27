package bo.sipqr.controlador;

import bo.sipqr.dto.baneco.ConsultaMovimientosSolicitud;
import bo.sipqr.dto.baneco.DesencriptarSolicitud;
import bo.sipqr.dto.baneco.EncriptarSolicitud;
import bo.sipqr.dto.baneco.EstadoConexionBaneco;
import bo.sipqr.dto.baneco.GenerarQrSolicitud;
import bo.sipqr.dto.baneco.TextoBanecoRespuesta;
import bo.sipqr.modelo.baneco.LotePagoSolicitud;
import bo.sipqr.modelo.baneco.RespuestaBaneco;
import bo.sipqr.modelo.baneco.RespuestaCargaPlanilla;
import bo.sipqr.modelo.baneco.RespuestaEstadoQr;
import bo.sipqr.modelo.baneco.RespuestaGeneracionQr;
import bo.sipqr.modelo.baneco.RespuestaMovimientosCuenta;
import bo.sipqr.modelo.baneco.RespuestaQrPagados;
import bo.sipqr.servicio.baneco.BanecoAutenticacionServicio;
import bo.sipqr.servicio.baneco.BanecoCifradoServicio;
import bo.sipqr.servicio.baneco.BanecoCuentaServicio;
import bo.sipqr.servicio.baneco.BanecoLoteServicio;
import bo.sipqr.servicio.baneco.BanecoQrServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/baneco")
@Tag(name = "BANECO", description = "Servicios internos de integración con API Market BANECO")
@SecurityRequirement(name = "bearerAuth")
public class BanecoControlador {
    private final BanecoCifradoServicio cifradoServicio;
    private final BanecoAutenticacionServicio autenticacionServicio;
    private final BanecoQrServicio qrServicio;
    private final BanecoCuentaServicio cuentaServicio;
    private final BanecoLoteServicio loteServicio;
    private final boolean diagnosticoHabilitado;

    public BanecoControlador(BanecoCifradoServicio cifradoServicio,
                             BanecoAutenticacionServicio autenticacionServicio,
                             BanecoQrServicio qrServicio,
                             BanecoCuentaServicio cuentaServicio,
                             BanecoLoteServicio loteServicio,
                             @Value("${baneco.diagnostico.habilitado:false}") boolean diagnosticoHabilitado) {
        this.cifradoServicio = cifradoServicio;
        this.autenticacionServicio = autenticacionServicio;
        this.qrServicio = qrServicio;
        this.cuentaServicio = cuentaServicio;
        this.loteServicio = loteServicio;
        this.diagnosticoHabilitado = diagnosticoHabilitado;
    }

    @PostMapping("/encriptar")
    @Operation(summary = "Encriptar texto", description = "Herramienta de diagnóstico; requiere habilitación explícita")
    public ResponseEntity<TextoBanecoRespuesta> encriptar(@Valid @RequestBody EncriptarSolicitud solicitud) {
        verificarDiagnostico();
        return ResponseEntity.ok(new TextoBanecoRespuesta(cifradoServicio.encriptar(solicitud.getTexto())));
    }

    @PostMapping("/desencriptar")
    @Operation(summary = "Desencriptar texto", description = "Herramienta de diagnóstico; requiere habilitación explícita")
    public ResponseEntity<TextoBanecoRespuesta> desencriptar(@Valid @RequestBody DesencriptarSolicitud solicitud) {
        verificarDiagnostico();
        return ResponseEntity.ok(new TextoBanecoRespuesta(cifradoServicio.desencriptar(solicitud.getTextoEncriptado())));
    }

    @PostMapping("/autenticar")
    @Operation(summary = "Verificar autenticación bancaria", description = "Comprueba la conectividad y autenticación con BANECO")
    public ResponseEntity<EstadoConexionBaneco> autenticar() {
        return ResponseEntity.ok(autenticacionServicio.verificarConexion());
    }

    @PostMapping("/generar-qr")
    @Operation(summary = "Generar QR", description = "Genera un QR de cobro y registra la operación para idempotencia")
    public ResponseEntity<RespuestaGeneracionQr> generarQr(@Valid @RequestBody GenerarQrSolicitud solicitud) {
        return ResponseEntity.ok(qrServicio.generarQr(solicitud));
    }

    @DeleteMapping("/anular-qr/{qrId}")
    @Operation(summary = "Anular QR", description = "Anula un QR existente y registra la operación para idempotencia")
    public ResponseEntity<RespuestaBaneco> anularQr(@PathVariable("qrId") String qrId) {
        return ResponseEntity.ok(qrServicio.anularQr(qrId));
    }

    @GetMapping("/estado-qr/{qrId}")
    @Operation(summary = "Consultar estado de QR")
    public ResponseEntity<RespuestaEstadoQr> estadoQr(@PathVariable("qrId") String qrId) {
        return ResponseEntity.ok(qrServicio.verificarEstadoQr(qrId));
    }

    @GetMapping("/qr-pagados/{fecha}")
    @Operation(summary = "Listar QR pagados", description = "Consulta los QR pagados en una fecha yyyy-MM-dd")
    public ResponseEntity<RespuestaQrPagados> qrPagados(
            @PathVariable("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(qrServicio.listarQrPagados(fecha));
    }

    @PostMapping("/movimientos-cuenta")
    @Operation(summary = "Consultar movimientos de cuenta")
    public ResponseEntity<RespuestaMovimientosCuenta> movimientos(@Valid @RequestBody ConsultaMovimientosSolicitud solicitud) {
        return ResponseEntity.ok(cuentaServicio.consultarMovimientos(solicitud));
    }

    @PostMapping("/lotes")
    @Operation(summary = "Cargar lote de pagos", description = "Envía una planilla de pagos y registra la operación para idempotencia")
    public ResponseEntity<RespuestaCargaPlanilla> cargarLote(@Valid @RequestBody LotePagoSolicitud solicitud) {
        return ResponseEntity.ok(loteServicio.cargarLote(solicitud));
    }

    private void verificarDiagnostico() {
        if (!diagnosticoHabilitado) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado");
        }
    }
}
