package bo.sipqr.servicio.baneco;

import bo.sipqr.cliente.baneco.BanecoClienteAutenticado;
import bo.sipqr.dto.baneco.ConsultaMovimientosSolicitud;
import bo.sipqr.modelo.baneco.RespuestaMovimientosCuenta;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class BanecoCuentaServicio {
    private final BanecoClienteAutenticado clienteAutenticado;
    private final BanecoCifradoServicio cifradoServicio;
    private final BanecoConfiguracionServicio configuracionServicio;
    private final BanecoRespuestaServicio respuestaServicio;
    private final BanecoValidacionServicio validacionServicio;

    public BanecoCuentaServicio(BanecoClienteAutenticado clienteAutenticado,
                                BanecoCifradoServicio cifradoServicio,
                                BanecoConfiguracionServicio configuracionServicio,
                                BanecoRespuestaServicio respuestaServicio,
                                BanecoValidacionServicio validacionServicio) {
        this.clienteAutenticado = clienteAutenticado;
        this.cifradoServicio = cifradoServicio;
        this.configuracionServicio = configuracionServicio;
        this.respuestaServicio = respuestaServicio;
        this.validacionServicio = validacionServicio;
    }

    public RespuestaMovimientosCuenta consultarMovimientos(ConsultaMovimientosSolicitud solicitud) {
        LocalDate inicio = validacionServicio.fecha(solicitud.getStartDate(), "La fecha inicial");
        LocalDate fin = validacionServicio.fecha(solicitud.getEndDate(), "La fecha final");
        validacionServicio.rangoFechas(inicio, fin);
        ConfiguracionBanco configuracion = configuracionServicio.obtener();
        Map<String, Object> cuerpo = new LinkedHashMap<String, Object>();
        cuerpo.put("accountCode", cifradoServicio.encriptar(configuracion.account()));
        cuerpo.put("startDate", inicio.toString());
        cuerpo.put("endDate", fin.toString());
        String json = clienteAutenticado.postJson("/api/accounts/queryMovements", cuerpo);
        return respuestaServicio.leerYValidar(json, RespuestaMovimientosCuenta.class);
    }
}
