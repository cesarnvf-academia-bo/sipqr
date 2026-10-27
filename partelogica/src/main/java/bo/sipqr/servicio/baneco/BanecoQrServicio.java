package bo.sipqr.servicio.baneco;

import bo.sipqr.cliente.baneco.BanecoClienteAutenticado;
import bo.sipqr.dto.baneco.GenerarQrSolicitud;
import bo.sipqr.modelo.baneco.BanecoOperacion;
import bo.sipqr.modelo.baneco.RespuestaBaneco;
import bo.sipqr.modelo.baneco.RespuestaEstadoQr;
import bo.sipqr.modelo.baneco.RespuestaGeneracionQr;
import bo.sipqr.modelo.baneco.RespuestaQrPagados;
import bo.sipqr.repositorio.baneco.BanecoOperacionRepositorio;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BanecoQrServicio {
    private static final Logger REGISTRO = LoggerFactory.getLogger(BanecoQrServicio.class);
    private static final String TIPO_GENERAR = "QR_GENERAR";
    private static final String TIPO_ANULAR = "QR_ANULAR";
    private static final DateTimeFormatter FORMATO_FECHA_API = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final BanecoClienteAutenticado clienteAutenticado;
    private final BanecoCifradoServicio cifradoServicio;
    private final BanecoConfiguracionServicio configuracionServicio;
    private final BanecoRespuestaServicio respuestaServicio;
    private final BanecoValidacionServicio validacionServicio;
    private final BanecoOperacionRepositorio operacionRepositorio;

    public BanecoQrServicio(BanecoClienteAutenticado clienteAutenticado,
                            BanecoCifradoServicio cifradoServicio,
                            BanecoConfiguracionServicio configuracionServicio,
                            BanecoRespuestaServicio respuestaServicio,
                            BanecoValidacionServicio validacionServicio,
                            BanecoOperacionRepositorio operacionRepositorio) {
        this.clienteAutenticado = clienteAutenticado;
        this.cifradoServicio = cifradoServicio;
        this.configuracionServicio = configuracionServicio;
        this.respuestaServicio = respuestaServicio;
        this.validacionServicio = validacionServicio;
        this.operacionRepositorio = operacionRepositorio;
    }

    @Transactional
    public RespuestaGeneracionQr generarQr(GenerarQrSolicitud solicitud) {
        String transactionId = validacionServicio.textoRequerido(solicitud.getTransactionId(),
            "El identificador de transacción", 150);
        validacionServicio.moneda(solicitud.getCurrency());
        validacionServicio.importe(solicitud.getAmount(), "El importe");
        validacionServicio.fecha(solicitud.getDueDate(), "La fecha de vencimiento");
        if (solicitud.getSingleUse() == null || solicitud.getModifyAmount() == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "Los indicadores singleUse y modifyAmount son obligatorios");
        }
        if (solicitud.getBranchCode() != null && solicitud.getBranchCode().trim().length() > 5) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "El código de sucursal no debe superar 5 caracteres");
        }
        verificarOperacionNoReutilizada(TIPO_GENERAR, transactionId);
        ConfiguracionBanco configuracion = configuracionServicio.obtener();
        String accountCredit = cifradoServicio.encriptar(configuracion.account());
        Map<String, Object> cuerpo = new LinkedHashMap<String, Object>();
        cuerpo.put("transactionId", transactionId);
        cuerpo.put("accountCredit", accountCredit);
        cuerpo.put("currency", solicitud.getCurrency().trim().toUpperCase());
        cuerpo.put("amount", solicitud.getAmount());
        if (solicitud.getDescription() != null && !solicitud.getDescription().isBlank()) {
            cuerpo.put("description", solicitud.getDescription().trim());
        }
        cuerpo.put("dueDate", solicitud.getDueDate().trim());
        cuerpo.put("singleUse", solicitud.getSingleUse());
        cuerpo.put("modifyAmount", solicitud.getModifyAmount());
        if (solicitud.getBranchCode() != null && !solicitud.getBranchCode().isBlank()) {
            cuerpo.put("branchCode", solicitud.getBranchCode().trim());
        }
        BanecoOperacion operacion = crearOperacion(TIPO_GENERAR, transactionId,
            respuestaServicio.hash(respuestaServicio.serializar(cuerpo)));
        try {
            String json = clienteAutenticado.postJson("/api/qrsimple/generateQR", cuerpo);
            RespuestaGeneracionQr respuesta = respuestaServicio.leerYValidar(json, RespuestaGeneracionQr.class);
            if (respuesta.getQrId() == null || respuesta.getQrId().isBlank()) {
                throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                    "El banco no devolvió el identificador del QR");
            }
            if (respuesta.getQrImage() == null || respuesta.getQrImage().isBlank()) {
                throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                    "El banco no devolvió la imagen del QR");
            }
            operacion.setEstado("EXITOSO");
            operacion.setQrId(respuesta.getQrId());
            operacion.setCodigoRespuesta(respuesta.getResponseCode());
            operacion.setMensajeRespuesta(respuesta.getMessage());
            operacionRepositorio.actualizarOperacion(operacion);
            REGISTRO.info("BANECO QR generado para transactionId {}", transactionId);
            return respuesta;
        } catch (RuntimeException ex) {
            marcarError(operacion, ex);
            throw ex;
        }
    }

    @Transactional
    public RespuestaBaneco anularQr(String qrId) {
        String identificador = validacionServicio.textoRequerido(qrId, "El identificador del QR", 100);
        var existente = operacionRepositorio.buscarOperacion(TIPO_ANULAR, identificador);
        if (existente.isPresent()) {
            if ("EXITOSO".equals(existente.get().getEstado())) {
                return new RespuestaBanecoConCodigo(0, "");
            }
            if ("PENDIENTE".equals(existente.get().getEstado())) {
                throw new BanecoExcepcion(TipoErrorBaneco.IDEMPOTENCIA,
                    "La anulación de este QR se encuentra en proceso");
            }
        }
        BanecoOperacion operacion = existente.orElseGet(() ->
            crearOperacion(TIPO_ANULAR, identificador, respuestaServicio.hash(identificador)));
        Map<String, Object> cuerpo = new LinkedHashMap<String, Object>();
        cuerpo.put("qrId", identificador);
        try {
            String json = clienteAutenticado.deleteJson("/api/qrsimple/cancelQR", cuerpo);
            RespuestaBaneco respuesta = respuestaServicio.leerYValidar(json, RespuestaBaneco.class);
            operacion.setEstado("EXITOSO");
            operacion.setQrId(identificador);
            operacion.setCodigoRespuesta(respuesta.getResponseCode());
            operacion.setMensajeRespuesta(respuesta.getMessage());
            operacionRepositorio.actualizarOperacion(operacion);
            REGISTRO.info("BANECO QR anulado {}", identificador);
            return respuesta;
        } catch (RuntimeException ex) {
            marcarError(operacion, ex);
            throw ex;
        }
    }

    public RespuestaEstadoQr verificarEstadoQr(String qrId) {
        String identificador = validacionServicio.textoRequerido(qrId, "El identificador del QR", 100);
        String json = clienteAutenticado.obtener("/api/qrsimple/v2/statusQR/" + identificador, Map.of());
        return respuestaServicio.leerYValidar(json, RespuestaEstadoQr.class);
    }

    public RespuestaQrPagados listarQrPagados(LocalDate fecha) {
        if (fecha == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, "La fecha es obligatoria");
        }
        String json = clienteAutenticado.obtener("/api/qrsimple/v2/paidQR/" + fecha.format(FORMATO_FECHA_API), Map.of());
        return respuestaServicio.leerYValidar(json, RespuestaQrPagados.class);
    }

    private void verificarOperacionNoReutilizada(String tipo, String transactionId) {
        if (operacionRepositorio.buscarOperacion(tipo, transactionId).isPresent()) {
            throw new BanecoExcepcion(TipoErrorBaneco.IDEMPOTENCIA,
                "El identificador de transacción ya fue utilizado");
        }
    }

    private BanecoOperacion crearOperacion(String tipo, String transactionId, String hash) {
        BanecoOperacion operacion = new BanecoOperacion();
        operacion.setTipo(tipo);
        operacion.setTransactionId(transactionId);
        operacion.setEstado("PENDIENTE");
        operacion.setHashSolicitud(hash);
        try {
            operacionRepositorio.insertarOperacion(operacion);
            return operacion;
        } catch (DuplicateKeyException ex) {
            throw new BanecoExcepcion(TipoErrorBaneco.IDEMPOTENCIA,
                "El identificador de transacción ya fue utilizado");
        }
    }

    private void marcarError(BanecoOperacion operacion, RuntimeException ex) {
        operacion.setEstado("ERROR");
        if (ex instanceof BanecoExcepcion baneco) {
            operacion.setCodigoRespuesta(baneco.getCodigoBanco());
            operacion.setMensajeRespuesta(baneco.getMessage());
        }
        operacionRepositorio.actualizarOperacion(operacion);
    }

    private static final class RespuestaBanecoConCodigo extends RespuestaBaneco {
        private RespuestaBanecoConCodigo(Integer codigo, String mensaje) {
            setResponseCode(codigo);
            setMessage(mensaje);
        }
    }
}
