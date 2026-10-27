package bo.sipqr.servicio.baneco;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class BanecoValidacionServicio {
    public String textoRequerido(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > longitudMaxima) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                campo + " no debe superar " + longitudMaxima + " caracteres");
        }
        return limpio;
    }

    public String moneda(String valor) {
        String moneda = textoRequerido(valor, "La moneda", 3).toUpperCase(Locale.ROOT);
        if (!moneda.equals("BOB") && !moneda.equals("USD")) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, "La moneda debe ser BOB o USD");
        }
        return moneda;
    }

    public BigDecimal importe(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, campo + " debe ser mayor a cero");
        }
        if (valor.scale() > 2) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION, campo + " debe tener como máximo dos decimales");
        }
        return valor;
    }

    public LocalDate fecha(String valor, String campo) {
        String fecha = textoRequerido(valor, campo, 10);
        try {
            return LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                campo + " debe tener el formato yyyy-MM-dd");
        }
    }

    public void rangoFechas(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new BanecoExcepcion(TipoErrorBaneco.VALIDACION,
                "La fecha inicial no puede ser posterior a la fecha final");
        }
    }
}
