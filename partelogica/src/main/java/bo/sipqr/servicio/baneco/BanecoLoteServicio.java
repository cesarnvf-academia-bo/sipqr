package bo.sipqr.servicio.baneco;

import bo.sipqr.cliente.baneco.BanecoClienteAutenticado;
import bo.sipqr.modelo.baneco.BanecoOperacion;
import bo.sipqr.modelo.baneco.BatchPayment;
import bo.sipqr.modelo.baneco.LotePagoSolicitud;
import bo.sipqr.modelo.baneco.RespuestaCargaPlanilla;
import bo.sipqr.repositorio.baneco.BanecoOperacionRepositorio;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BanecoLoteServicio {
    private static final Logger REGISTRO = LoggerFactory.getLogger(BanecoLoteServicio.class);
    private static final String TIPO_LOTE = "LOTE_CARGAR";

    private final BanecoClienteAutenticado clienteAutenticado;
    private final BanecoConfiguracionServicio configuracionServicio;
    private final BanecoRespuestaServicio respuestaServicio;
    private final BanecoValidacionServicio validacionServicio;
    private final BanecoOperacionRepositorio operacionRepositorio;

    public BanecoLoteServicio(BanecoClienteAutenticado clienteAutenticado,
                              BanecoConfiguracionServicio configuracionServicio,
                              BanecoRespuestaServicio respuestaServicio,
                              BanecoValidacionServicio validacionServicio,
                              BanecoOperacionRepositorio operacionRepositorio) {
        this.clienteAutenticado = clienteAutenticado;
        this.configuracionServicio = configuracionServicio;
        this.respuestaServicio = respuestaServicio;
        this.validacionServicio = validacionServicio;
        this.operacionRepositorio = operacionRepositorio;
    }

    @Transactional
    public RespuestaCargaPlanilla cargarLote(LotePagoSolicitud solicitud) {
        String batchId = validacionServicio.textoRequerido(solicitud.getBatchId(),
            "El identificador del lote", 150);
        validacionServicio.textoRequerido(solicitud.getType(), "El tipo de lote", 20);
        validacionServicio.moneda(solicitud.getBatchCurrency());
        BigDecimal total = validacionServicio.importe(solicitud.getBatchAmount(), "El importe total del lote");
        List<BatchPayment> pagos = solicitud.getPaymentList();
        if (pagos == null || pagos.isEmpty()) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "El lote debe contener al menos un pago");
        }
        if (solicitud.getPaymentCount() != null && solicitud.getPaymentCount() != pagos.size()) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "La cantidad de pagos no coincide con el detalle del lote");
        }
        solicitud.setPaymentCount(pagos.size());
        if (solicitud.getDetailedDebit() == null) {
            solicitud.setDetailedDebit(Boolean.FALSE);
        }
        if (solicitud.getAccountCode() == null || solicitud.getAccountCode().isBlank()) {
            solicitud.setAccountCode(configuracionServicio.obtener().account());
        }
        BigDecimal suma = BigDecimal.ZERO;
        List<BatchPayment> normalizados = new ArrayList<BatchPayment>();
        for (BatchPayment pago : pagos) {
            if (pago == null) {
                throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, "El detalle del lote contiene un pago vacío");
            }
            validacionServicio.textoRequerido(pago.getBatchDetailId(), "El identificador de detalle", 150);
            validacionServicio.importe(pago.getAmount(), "El importe del pago " + pago.getBatchDetailId());
            validacionServicio.textoRequerido(pago.getBankCode(), "El código de banco", 20);
            validacionServicio.textoRequerido(pago.getBeneficiaryName(), "El nombre del beneficiario", 200);
            validacionServicio.textoRequerido(pago.getAccountTypeCode(), "El tipo de cuenta", 10);
            if (pago.getAccountCode() == null || pago.getAccountCode().isBlank()) {
                pago.setAccountCode(solicitud.getAccountCode());
            }
            suma = suma.add(pago.getAmount());
            normalizados.add(pago);
        }
        if (suma.compareTo(total) != 0) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "La suma de los pagos no coincide con el importe total del lote");
        }
        verificarOperacionNoReutilizada(batchId);
        BanecoOperacion operacion = crearOperacion(batchId,
            respuestaServicio.hash(respuestaServicio.serializar(solicitud)));
        try {
            String json = clienteAutenticado.postJson("/api/batchPayment/upload", solicitud);
            RespuestaCargaPlanilla respuesta = respuestaServicio.leerYValidar(json, RespuestaCargaPlanilla.class);
            if (respuesta.getBankBatchId() == null) {
                throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                    "El banco no devolvió el identificador del lote procesado");
            }
            operacion.setEstado("EXITOSO");
            operacion.setBatchId(batchId);
            operacion.setBancoBatchId(respuesta.getBankBatchId());
            operacion.setCodigoRespuesta(respuesta.getResponseCode());
            operacion.setMensajeRespuesta(respuesta.getMessage());
            operacionRepositorio.actualizarOperacion(operacion);
            REGISTRO.info("BANECO lote {} enviado con bankBatchId {}", batchId, respuesta.getBankBatchId());
            return respuesta;
        } catch (RuntimeException ex) {
            marcarError(operacion, ex);
            throw ex;
        }
    }

    private void verificarOperacionNoReutilizada(String batchId) {
        if (operacionRepositorio.buscarOperacion(TIPO_LOTE, batchId).isPresent()) {
            throw new BanecoExcepcion(TipoErrorBaneco.IDEMPOTENCIA,
                "El identificador del lote ya fue utilizado");
        }
    }

    private BanecoOperacion crearOperacion(String batchId, String hash) {
        BanecoOperacion operacion = new BanecoOperacion();
        operacion.setTipo(TIPO_LOTE);
        operacion.setTransactionId(batchId);
        operacion.setBatchId(batchId);
        operacion.setEstado("PENDIENTE");
        operacion.setHashSolicitud(hash);
        try {
            operacionRepositorio.insertarOperacion(operacion);
            return operacion;
        } catch (DuplicateKeyException ex) {
            throw new BanecoExcepcion(TipoErrorBaneco.IDEMPOTENCIA,
                "El identificador del lote ya fue utilizado");
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
}
