package bo.sipqr.modelo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PagoQrDetalle {
    private Long id;
    private Long pagoQrId;
    private String identificadorPagoBancario;
    private OffsetDateTime fechaPago;
    private BigDecimal importe;
    private String moneda;
    private String datosPago;
    private OffsetDateTime fechaRecepcion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPagoQrId() {
        return pagoQrId;
    }

    public void setPagoQrId(Long pagoQrId) {
        this.pagoQrId = pagoQrId;
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

    public String getDatosPago() {
        return datosPago;
    }

    public void setDatosPago(String datosPago) {
        this.datosPago = datosPago;
    }

    public OffsetDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(OffsetDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }
}
