package bo.sipqr.servicio;

import bo.sipqr.dto.PagoQrSolicitud;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.BanecoValidacionServicio;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PagoQrValidacionServicio {
    private static final int LONGITUD_MAXIMA_IMAGEN = 8_000_000;
    private static final byte[] FIRMA_PNG = {
        (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
        (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
    };
    private static final Set<String> ESTADOS = Set.of(
        "REGISTRADO", "GENERANDO", "PENDIENTE", "PAGADO", "ANULADO", "VENCIDO", "ERROR");

    private final BanecoValidacionServicio banecoValidacionServicio;

    public PagoQrValidacionServicio(BanecoValidacionServicio banecoValidacionServicio) {
        this.banecoValidacionServicio = banecoValidacionServicio;
    }

    public void validarSolicitud(PagoQrSolicitud solicitud) {
        if (solicitud == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, "La solicitud es obligatoria");
        }
        banecoValidacionServicio.importe(solicitud.getAmount(), "El importe");
        solicitud.setCurrency(banecoValidacionServicio.moneda(solicitud.getCurrency()));
        if (solicitud.getDueDate() == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, "La fecha de vencimiento es obligatoria");
        }
        if (solicitud.getDueDate().isBefore(LocalDate.now())) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "La fecha de vencimiento no puede ser anterior a hoy");
        }
        if (solicitud.getSingleUse() == null || solicitud.getModifyAmount() == null) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "Los indicadores singleUse y modifyAmount son obligatorios");
        }
        solicitud.setDescription(normalizarTexto(solicitud.getDescription(), 500, "La descripción"));
        solicitud.setBranchCode(normalizarTexto(solicitud.getBranchCode(), 5, "El código de sucursal"));
    }

    public String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        String normalizado = estado.trim().toUpperCase(Locale.ROOT);
        if (!ESTADOS.contains(normalizado)) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, "El estado indicado no es válido");
        }
        return normalizado;
    }

    public String limpiarImagenQr(String imagen) {
        if (imagen == null || imagen.isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "El banco no devolvió la imagen del QR");
        }
        String contenido = imagen.trim();
        if (contenido.regionMatches(true, 0, "data:", 0, 5)) {
            int separador = contenido.indexOf(',');
            if (separador < 0 || !contenido.substring(0, separador).equalsIgnoreCase("data:image/png;base64")) {
                throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                    "La imagen del QR no tiene un formato PNG Base64 válido");
            }
            contenido = contenido.substring(separador + 1);
        }
        String compacto = contenido.replaceAll("\\s+", "");
        if (compacto.isEmpty() || compacto.length() > LONGITUD_MAXIMA_IMAGEN) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "La imagen del QR no tiene un tamaño válido");
        }
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(compacto);
        } catch (IllegalArgumentException ex) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "La imagen del QR no es un Base64 válido");
        }
        if (bytes.length < FIRMA_PNG.length) {
            throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                "La imagen del QR no es un PNG válido");
        }
        for (int indice = 0; indice < FIRMA_PNG.length; indice++) {
            if (bytes[indice] != FIRMA_PNG[indice]) {
                throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                    "La imagen del QR no es un PNG válido");
            }
        }
        return compacto;
    }

    private String normalizarTexto(String valor, int longitudMaxima, String campo) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        if (limpio.length() > longitudMaxima) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                campo + " no debe superar " + longitudMaxima + " caracteres");
        }
        return limpio;
    }
}
