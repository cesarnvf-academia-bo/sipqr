package bo.sipqr.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

public class PagoQrDetalleRespuesta {
    private Long id;
    private String identificadorPagoBancario;
    private OffsetDateTime fechaPago;
    private BigDecimal importe;
    private String moneda;
    private Map<String, Object> datosPago;
    private OffsetDateTime fechaRecepcion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificadorPagoBancario() {
        return identificadorPagoBancario;
    }

    public void setIdentificadorPagoBancario(String identificadorPagoBancario) {
        this.identificadorPagoBancario = identificadorPagoBancario;
    }

    public OffsetDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(OffsetDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Map<String, Object> getDatosPago() {
        return datosPago;
    }

    public void setDatosPago(Map<String, Object> datosPago) {
        this.datosPago = datosPago;
    }

    public OffsetDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(OffsetDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }
}
