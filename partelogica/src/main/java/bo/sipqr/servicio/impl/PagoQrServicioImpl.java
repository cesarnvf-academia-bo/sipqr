package bo.sipqr.servicio.impl;

import bo.sipqr.dto.PaginaRespuesta;
import bo.sipqr.dto.PagoQrDetalleRespuesta;
import bo.sipqr.dto.PagoQrRespuesta;
import bo.sipqr.dto.PagoQrSolicitud;
import bo.sipqr.dto.baneco.GenerarQrSolicitud;
import bo.sipqr.modelo.PagoQr;
import bo.sipqr.modelo.PagoQrDetalle;
import bo.sipqr.modelo.baneco.PaymentQR;
import bo.sipqr.modelo.baneco.RespuestaBaneco;
import bo.sipqr.modelo.baneco.RespuestaEstadoQr;
import bo.sipqr.modelo.baneco.RespuestaGeneracionQr;
import bo.sipqr.repositorio.PagoQrDetalleRepositorio;
import bo.sipqr.repositorio.PagoQrRepositorio;
import bo.sipqr.servicio.PagoQrEstadoServicio;
import bo.sipqr.servicio.PagoQrServicio;
import bo.sipqr.servicio.PagoQrValidacionServicio;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.BanecoQrServicio;
import bo.sipqr.servicio.baneco.BanecoRespuestaServicio;
import bo.sipqr.servicio.baneco.BanecoValidacionServicio;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@Service
public class PagoQrServicioImpl implements PagoQrServicio {
    private static final String PREFIJO_TRANSACCION = "PGQ-";
    private static final BigDecimal CERO = new BigDecimal("0.00");

    private final PagoQrRepositorio pagoQrRepositorio;
    private final PagoQrDetalleRepositorio pagoQrDetalleRepositorio;
    private final BanecoQrServicio banecoQrServicio;
    private final PagoQrValidacionServicio pagoQrValidacionServicio;
    private final PagoQrEstadoServicio pagoQrEstadoServicio;
    private final BanecoValidacionServicio banecoValidacionServicio;
    private final BanecoRespuestaServicio banecoRespuestaServicio;
    private final ObjectMapper objectMapper;

    public PagoQrServicioImpl(PagoQrRepositorio pagoQrRepositorio,
                              PagoQrDetalleRepositorio pagoQrDetalleRepositorio,
                              BanecoQrServicio banecoQrServicio,
                              PagoQrValidacionServicio pagoQrValidacionServicio,
                              PagoQrEstadoServicio pagoQrEstadoServicio,
                              BanecoValidacionServicio banecoValidacionServicio,
                              BanecoRespuestaServicio banecoRespuestaServicio,
                              ObjectMapper objectMapper) {
        this.pagoQrRepositorio = pagoQrRepositorio;
        this.pagoQrDetalleRepositorio = pagoQrDetalleRepositorio;
        this.banecoQrServicio = banecoQrServicio;
        this.pagoQrValidacionServicio = pagoQrValidacionServicio;
        this.pagoQrEstadoServicio = pagoQrEstadoServicio;
        this.banecoValidacionServicio = banecoValidacionServicio;
        this.banecoRespuestaServicio = banecoRespuestaServicio;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaginaRespuesta<PagoQrRespuesta> encontrarTodos(String buscar, String estado, int pagina, int limite) {
        int paginaValida = pagina < 1 ? 1 : pagina;
        int limiteValido = (limite == 5 || limite == 10 || limite == 20) ? limite : 5;
        int desplazamiento = (paginaValida - 1) * limiteValido;
        String filtro = (buscar == null || buscar.isBlank()) ? null : buscar.trim();
        String estadoFiltro = pagoQrValidacionServicio.normalizarEstado(estado);
        List<PagoQr> pagos = pagoQrRepositorio.encontrarTodos(filtro, estadoFiltro, limiteValido, desplazamiento);
        long total = pagoQrRepositorio.contar(filtro, estadoFiltro);
        List<PagoQrRespuesta> datos = pagos.stream()
            .map(pago -> construirRespuesta(pago, null))
            .collect(Collectors.toList());
        return new PaginaRespuesta<PagoQrRespuesta>(datos, paginaValida, limiteValido, total);
    }

    @Override
    public PagoQrRespuesta encontrarPorId(Long id) {
        PagoQr pagoQr = buscarPagoQr(id);
        return construirRespuesta(pagoQr, pagoQrDetalleRepositorio.encontrarPorPagoQrId(id));
    }

    @Override
    public PagoQrRespuesta generar(PagoQrSolicitud solicitud) {
        pagoQrValidacionServicio.validarSolicitud(solicitud);
        PagoQr pagoQr = new PagoQr();
        pagoQr.setTransactionId(PREFIJO_TRANSACCION + UUID.randomUUID().toString().replace("-", ""));
        pagoQr.setAmount(solicitud.getAmount());
        pagoQr.setCurrency(solicitud.getCurrency());
        pagoQr.setDescription(solicitud.getDescription());
        pagoQr.setDueDate(solicitud.getDueDate());
        pagoQr.setSingleUse(solicitud.getSingleUse());
        pagoQr.setModifyAmount(solicitud.getModifyAmount());
        pagoQr.setBranchCode(solicitud.getBranchCode());
        pagoQr.setEstado(PagoQrEstadoServicio.REGISTRADO);
        pagoQr.setResultadoIncierto(Boolean.FALSE);
        try {
            pagoQrRepositorio.insertar(pagoQr);
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El identificador de transacción ya fue utilizado");
        }
        if (pagoQrRepositorio.marcarGenerando(pagoQr.getId()) != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El pago QR ya está siendo procesado");
        }
        pagoQr.setEstado(PagoQrEstadoServicio.GENERANDO);

        GenerarQrSolicitud solicitudBanco = new GenerarQrSolicitud();
        solicitudBanco.setTransactionId(pagoQr.getTransactionId());
        solicitudBanco.setCurrency(pagoQr.getCurrency());
        solicitudBanco.setAmount(pagoQr.getAmount());
        solicitudBanco.setDueDate(pagoQr.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        solicitudBanco.setSingleUse(pagoQr.getSingleUse());
        solicitudBanco.setModifyAmount(pagoQr.getModifyAmount());
        solicitudBanco.setDescription(pagoQr.getDescription());
        solicitudBanco.setBranchCode(pagoQr.getBranchCode());

        RespuestaGeneracionQr respuesta;
        try {
            respuesta = banecoQrServicio.generarQr(solicitudBanco);
        } catch (BanecoExcepcion ex) {
            marcarError(pagoQr.getId(), null, ex, esResultadoIncierto(ex.getTipo()));
            throw ex;
        } catch (RuntimeException ex) {
            marcarError(pagoQr.getId(), null, null, true);
            throw ex;
        }

        try {
            pagoQr.setQrId(respuesta.getQrId());
            pagoQr.setQrImage(pagoQrValidacionServicio.limpiarImagenQr(respuesta.getQrImage()));
        } catch (BanecoExcepcion ex) {
            marcarError(pagoQr.getId(), pagoQr.getQrId(), ex, true);
            throw ex;
        }
        pagoQr.setEstado(PagoQrEstadoServicio.PENDIENTE);
        pagoQr.setResponseCode(codigoTexto(respuesta.getResponseCode()));
        pagoQr.setResponseMessage(respuesta.getMessage());
        pagoQr.setResultadoIncierto(Boolean.FALSE);
        if (pagoQrRepositorio.actualizarGeneracion(pagoQr) != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El pago QR cambió de estado durante la generación");
        }
        return encontrarPorId(pagoQr.getId());
    }

    @Override
    public PagoQrRespuesta verificar(Long id) {
        PagoQr pagoQr = buscarPagoQr(id);
        if (PagoQrEstadoServicio.PAGADO.equals(pagoQr.getEstado())
                || PagoQrEstadoServicio.ANULADO.equals(pagoQr.getEstado())) {
            return construirRespuesta(pagoQr, pagoQrDetalleRepositorio.encontrarPorPagoQrId(id));
        }
        if (pagoQr.getQrId() == null || pagoQr.getQrId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El pago QR no tiene un identificador bancario para verificar");
        }

        RespuestaEstadoQr respuesta = banecoQrServicio.verificarEstadoQr(pagoQr.getQrId());
        procesarPagos(pagoQr, respuesta.getPayment());
        BigDecimal totalPagado = pagoQrDetalleRepositorio.sumarImportes(pagoQr.getId());
        String nuevoEstado = pagoQrEstadoServicio.determinarEstado(pagoQr, respuesta, totalPagado, LocalDate.now());
        OffsetDateTime ahora = OffsetDateTime.now();

        pagoQr.setEstado(nuevoEstado);
        pagoQr.setResponseCode(codigoTexto(respuesta.getResponseCode()));
        pagoQr.setResponseMessage(respuesta.getMessage());
        pagoQr.setResultadoIncierto(Boolean.FALSE);
        pagoQr.setFechaUltimaVerificacion(ahora);
        if (PagoQrEstadoServicio.PAGADO.equals(nuevoEstado)) {
            pagoQr.setFechaPago(calcularFechaPago(pagoQr.getId(), ahora));
        }
        if (PagoQrEstadoServicio.ANULADO.equals(nuevoEstado)) {
            pagoQr.setFechaAnulacion(ahora);
        }
        pagoQrRepositorio.actualizarVerificacion(pagoQr);
        return encontrarPorId(id);
    }

    @Override
    public PagoQrRespuesta anular(Long id) {
        PagoQr pagoQr = buscarPagoQr(id);
        if (PagoQrEstadoServicio.ANULADO.equals(pagoQr.getEstado())) {
            return construirRespuesta(pagoQr, pagoQrDetalleRepositorio.encontrarPorPagoQrId(id));
        }
        if (!PagoQrEstadoServicio.PENDIENTE.equals(pagoQr.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Solo se puede anular un pago QR pendiente");
        }
        if (pagoQr.getQrId() == null || pagoQr.getQrId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El pago QR no tiene un identificador bancario para anular");
        }

        RespuestaBaneco respuesta = banecoQrServicio.anularQr(pagoQr.getQrId());
        pagoQr.setEstado(PagoQrEstadoServicio.ANULADO);
        pagoQr.setResponseCode(codigoTexto(respuesta.getResponseCode()));
        pagoQr.setResponseMessage(respuesta.getMessage());
        pagoQr.setResultadoIncierto(Boolean.FALSE);
        pagoQr.setFechaAnulacion(OffsetDateTime.now());
        if (pagoQrRepositorio.actualizarAnulacion(pagoQr) != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El pago QR cambió de estado durante la anulación");
        }
        return encontrarPorId(id);
    }

    @Override
    public void borrar(Long id) {
        PagoQr pagoQr = buscarPagoQr(id);
        if (!PagoQrEstadoServicio.REGISTRADO.equals(pagoQr.getEstado()) || pagoQr.getQrId() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Solo se puede borrar un pago QR registrado y sin QR generado");
        }
        if (pagoQrRepositorio.borrarRegistrado(id) != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No fue posible borrar el pago QR");
        }
    }

    private void procesarPagos(PagoQr pagoQr, List<PaymentQR> pagos) {
        if (pagos == null) {
            return;
        }
        for (PaymentQR pago : pagos) {
            if (pago == null) {
                continue;
            }
            if (pago.getQrId() != null && !pago.getQrId().isBlank()
                    && !pago.getQrId().equals(pagoQr.getQrId())) {
                throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                    "El banco devolvió un pago que no corresponde al QR consultado");
            }
            String identificador = banecoValidacionServicio.textoRequerido(pago.getTransactionId(),
                "El identificador de transacción del pago", 150);
            BigDecimal importe = banecoValidacionServicio.importe(pago.getAmount(), "El importe del pago");
            String moneda = pago.getCurrency() == null || pago.getCurrency().isBlank()
                ? pagoQr.getCurrency()
                : banecoValidacionServicio.moneda(pago.getCurrency());
            PagoQrDetalle detalle = new PagoQrDetalle();
            detalle.setPagoQrId(pagoQr.getId());
            detalle.setIdentificadorPagoBancario(identificador);
            detalle.setFechaPago(parsearFechaPago(pago));
            detalle.setImporte(importe);
            detalle.setMoneda(moneda);
            detalle.setDatosPago(banecoRespuestaServicio.serializar(pago));
            pagoQrDetalleRepositorio.insertarSiNoExiste(detalle);
        }
    }

    private OffsetDateTime parsearFechaPago(PaymentQR pago) {
        String fecha = pago.getPaymentDate();
        if (fecha == null || fecha.isBlank()) {
            return OffsetDateTime.now();
        }
        String fechaLimpia = fecha.trim();
        try {
            return OffsetDateTime.parse(fechaLimpia);
        } catch (DateTimeParseException ex) {
            // Se intenta con la hora separada o con fecha sin zona horaria.
        }
        String hora = pago.getPaymentTime();
        if (hora != null && !hora.isBlank()) {
            try {
                return LocalDateTime.parse(fechaLimpia + "T" + hora.trim())
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime();
            } catch (DateTimeParseException ex) {
                // Se intenta con la fecha sin hora.
            }
        }
        try {
            return LocalDateTime.parse(fechaLimpia)
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
        } catch (DateTimeParseException ex) {
            // Se intenta con la fecha sin hora.
        }
        try {
            return LocalDate.parse(fechaLimpia)
                .atStartOfDay(ZoneId.systemDefault())
                .toOffsetDateTime();
        } catch (DateTimeParseException ex) {
            return OffsetDateTime.now();
        }
    }

    private OffsetDateTime calcularFechaPago(Long pagoQrId, OffsetDateTime predeterminada) {
        OffsetDateTime fecha = null;
        for (PagoQrDetalle detalle : pagoQrDetalleRepositorio.encontrarPorPagoQrId(pagoQrId)) {
            if (detalle.getFechaPago() != null && (fecha == null || detalle.getFechaPago().isAfter(fecha))) {
                fecha = detalle.getFechaPago();
            }
        }
        return fecha == null ? predeterminada : fecha;
    }

    private PagoQr buscarPagoQr(Long id) {
        return pagoQrRepositorio.encontrarPorId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago QR no encontrado"));
    }

    private PagoQrRespuesta construirRespuesta(PagoQr pagoQr, List<PagoQrDetalle> detalles) {
        PagoQrRespuesta respuesta = new PagoQrRespuesta();
        respuesta.setId(pagoQr.getId());
        respuesta.setTransactionId(pagoQr.getTransactionId());
        respuesta.setQrId(pagoQr.getQrId());
        respuesta.setAmount(pagoQr.getAmount());
        respuesta.setCurrency(pagoQr.getCurrency());
        respuesta.setDescription(pagoQr.getDescription());
        respuesta.setDueDate(pagoQr.getDueDate());
        respuesta.setSingleUse(pagoQr.getSingleUse());
        respuesta.setModifyAmount(pagoQr.getModifyAmount());
        respuesta.setBranchCode(pagoQr.getBranchCode());
        respuesta.setEstado(pagoQr.getEstado());
        respuesta.setQrImage(pagoQr.getQrImage());
        respuesta.setResponseCode(pagoQr.getResponseCode());
        respuesta.setResponseMessage(pagoQr.getResponseMessage());
        respuesta.setResultadoIncierto(pagoQr.getResultadoIncierto());
        respuesta.setFechaCreacion(pagoQr.getFechaCreacion());
        respuesta.setFechaActualizacion(pagoQr.getFechaActualizacion());
        respuesta.setFechaUltimaVerificacion(pagoQr.getFechaUltimaVerificacion());
        respuesta.setFechaPago(pagoQr.getFechaPago());
        respuesta.setFechaAnulacion(pagoQr.getFechaAnulacion());
        BigDecimal total = detalles == null ? pagoQr.getTotalPagado() : sumar(detalles);
        respuesta.setTotalPagado(total == null ? CERO : total);
        respuesta.setPuedeGenerar(PagoQrEstadoServicio.REGISTRADO.equals(pagoQr.getEstado()));
        respuesta.setPuedeVerificar(pagoQr.getQrId() != null && !pagoQr.getQrId().isBlank()
            && (PagoQrEstadoServicio.PENDIENTE.equals(pagoQr.getEstado())
                || PagoQrEstadoServicio.VENCIDO.equals(pagoQr.getEstado())
                || PagoQrEstadoServicio.ERROR.equals(pagoQr.getEstado())));
        respuesta.setPuedeAnular(PagoQrEstadoServicio.PENDIENTE.equals(pagoQr.getEstado()));
        respuesta.setPuedeBorrar(PagoQrEstadoServicio.REGISTRADO.equals(pagoQr.getEstado())
            && pagoQr.getQrId() == null);
        if (detalles != null) {
            List<PagoQrDetalleRespuesta> pagos = new ArrayList<PagoQrDetalleRespuesta>();
            for (PagoQrDetalle detalle : detalles) {
                pagos.add(mapearDetalle(detalle));
            }
            respuesta.setPagos(pagos);
        }
        return respuesta;
    }

    private PagoQrDetalleRespuesta mapearDetalle(PagoQrDetalle detalle) {
        PagoQrDetalleRespuesta respuesta = new PagoQrDetalleRespuesta();
        respuesta.setId(detalle.getId());
        respuesta.setIdentificadorPagoBancario(detalle.getIdentificadorPagoBancario());
        respuesta.setFechaPago(detalle.getFechaPago());
        respuesta.setImporte(detalle.getImporte());
        respuesta.setMoneda(detalle.getMoneda());
        respuesta.setDatosPago(leerDatosPago(detalle.getDatosPago()));
        respuesta.setFechaRecepcion(detalle.getFechaRecepcion());
        return respuesta;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> leerDatosPago(String datosPago) {
        if (datosPago == null || datosPago.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(datosPago, Map.class);
        } catch (RuntimeException ex) {
            return Map.of();
        }
    }

    private BigDecimal sumar(List<PagoQrDetalle> detalles) {
        BigDecimal total = CERO;
        for (PagoQrDetalle detalle : detalles) {
            if (detalle.getImporte() != null) {
                total = total.add(detalle.getImporte());
            }
        }
        return total;
    }

    private void marcarError(Long id, String qrId, BanecoExcepcion excepcion, boolean resultadoIncierto) {
        String codigo = excepcion == null ? null : codigoTexto(excepcion.getCodigoBanco());
        String mensaje = excepcion == null ? "No fue posible completar la operación bancaria" : excepcion.getMessage();
        pagoQrRepositorio.actualizarError(id, qrId, codigo, mensaje, resultadoIncierto);
    }

    private boolean esResultadoIncierto(TipoErrorBaneco tipo) {
        return tipo == TipoErrorBaneco.COMUNICACION
            || tipo == TipoErrorBaneco.AUTENTICACION
            || tipo == TipoErrorBaneco.RESPUESTA_INVALIDA
            || tipo == TipoErrorBaneco.IDEMPOTENCIA;
    }

    private String codigoTexto(Integer codigo) {
        return codigo == null ? null : String.valueOf(codigo);
    }
}
