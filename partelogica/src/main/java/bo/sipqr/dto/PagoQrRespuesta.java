package bo.sipqr.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class PagoQrRespuesta {
    private Long id;
    private String transactionId;
    private String qrId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDate dueDate;
    private Boolean singleUse;
    private Boolean modifyAmount;
    private String branchCode;
    private String estado;
    private String qrImage;
    private String responseCode;
    private String responseMessage;
    private Boolean resultadoIncierto;
    private BigDecimal totalPagado;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
    private OffsetDateTime fechaUltimaVerificacion;
    private OffsetDateTime fechaPago;
    private OffsetDateTime fechaAnulacion;
    private boolean puedeGenerar;
    private boolean puedeVerificar;
    private boolean puedeAnular;
    private boolean puedeBorrar;
    private List<PagoQrDetalleRespuesta> pagos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getSingleUse() {
        return singleUse;
    }

    public void setSingleUse(Boolean singleUse) {
        this.singleUse = singleUse;
    }

    public Boolean getModifyAmount() {
        return modifyAmount;
    }

    public void setModifyAmount(Boolean modifyAmount) {
        this.modifyAmount = modifyAmount;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getQrImage() {
        return qrImage;
    }

    public void setQrImage(String qrImage) {
        this.qrImage = qrImage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Boolean getResultadoIncierto() {
        return resultadoIncierto;
    }

    public void setResultadoIncierto(Boolean resultadoIncierto) {
        this.resultadoIncierto = resultadoIncierto;
    }

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public OffsetDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public OffsetDateTime getFechaUltimaVerificacion() {
        return fechaUltimaVerificacion;
    }

    public void setFechaUltimaVerificacion(OffsetDateTime fechaUltimaVerificacion) {
        this.fechaUltimaVerificacion = fechaUltimaVerificacion;
    }

    public OffsetDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(OffsetDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public OffsetDateTime getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(OffsetDateTime fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public boolean isPuedeGenerar() {
        return puedeGenerar;
    }

    public void setPuedeGenerar(boolean puedeGenerar) {
        this.puedeGenerar = puedeGenerar;
    }

    public boolean isPuedeVerificar() {
        return puedeVerificar;
    }

    public void setPuedeVerificar(boolean puedeVerificar) {
        this.puedeVerificar = puedeVerificar;
    }

    public boolean isPuedeAnular() {
        return puedeAnular;
    }

    public void setPuedeAnular(boolean puedeAnular) {
        this.puedeAnular = puedeAnular;
    }

    public boolean isPuedeBorrar() {
        return puedeBorrar;
    }

    public void setPuedeBorrar(boolean puedeBorrar) {
        this.puedeBorrar = puedeBorrar;
    }

    public List<PagoQrDetalleRespuesta> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoQrDetalleRespuesta> pagos) {
        this.pagos = pagos;
    }
}
