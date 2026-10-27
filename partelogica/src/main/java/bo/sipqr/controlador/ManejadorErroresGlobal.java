package bo.sipqr.controlador;

import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ManejadorErroresGlobal {
    private static final Logger REGISTRO = LoggerFactory.getLogger(ManejadorErroresGlobal.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarEstado(ResponseStatusException excepcion) {
        String mensaje = excepcion.getReason() == null ? "No fue posible completar la operación" : excepcion.getReason();
        return construir(excepcion.getStatusCode().value(), mensaje);
    }

    @ExceptionHandler(BanecoExcepcion.class)
    public ResponseEntity<Map<String, Object>> manejarBaneco(BanecoExcepcion excepcion) {
        int estado = excepcion.getEstadoHttp() == null
            ? estadoPredeterminado(excepcion.getTipo())
            : excepcion.getEstadoHttp();
        REGISTRO.warn("Error BANECO tipo={} codigo={} estado={}",
            excepcion.getTipo(), excepcion.getCodigoBanco(), estado);
        Map<String, Object> respuesta = cuerpo(estado, excepcion.getMessage());
        if (excepcion.getCodigoBanco() != null) {
            respuesta.put("codigoBanco", excepcion.getCodigoBanco());
        }
        return ResponseEntity.status(estado).body(respuesta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException excepcion) {
        String mensaje = excepcion.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage() == null ? "Solicitud inválida" : error.getDefaultMessage())
            .orElse("Solicitud inválida");
        return construir(HttpStatus.BAD_REQUEST.value(), mensaje);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroInvalido(MethodArgumentTypeMismatchException excepcion) {
        return construir(HttpStatus.BAD_REQUEST.value(), "El parámetro " + excepcion.getName() + " no es válido");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarCuerpoInvalido(HttpMessageNotReadableException excepcion) {
        return construir(HttpStatus.BAD_REQUEST.value(), "El cuerpo de la solicitud no tiene un formato válido");
    }

    private int estadoPredeterminado(TipoErrorBaneco tipo) {
        return switch (tipo) {
            case VALIDACION -> HttpStatus.BAD_REQUEST.value();
            case CONFIGURACION -> HttpStatus.SERVICE_UNAVAILABLE.value();
            case COMUNICACION, AUTENTICACION, RESPUESTA_INVALIDA -> HttpStatus.BAD_GATEWAY.value();
            case OPERACION_RECHAZADA -> HttpStatus.UNPROCESSABLE_ENTITY.value();
            case IDEMPOTENCIA -> HttpStatus.CONFLICT.value();
            case NO_ENCONTRADO -> HttpStatus.NOT_FOUND.value();
            case WEBHOOK -> HttpStatus.UNAUTHORIZED.value();
        };
    }

    private ResponseEntity<Map<String, Object>> construir(int estado, String mensaje) {
        return ResponseEntity.status(estado).body(cuerpo(estado, mensaje));
    }

    private Map<String, Object> cuerpo(int estado, String mensaje) {
        Map<String, Object> respuesta = new LinkedHashMap<String, Object>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("estado", estado);
        respuesta.put("fecha", OffsetDateTime.now());
        return respuesta;
    }
}
