package bo.sipqr.modelo.baneco;

import java.time.OffsetDateTime;

public class BanecoDetalleLote {
    private Long id;
    private Long bancoBatchId;
    private String batchId;
    private String batchDetailId;
    private String estado;
    private String descripcionEstado;
    private String transactionIdDebito;
    private String transactionIdCredito;
    private OffsetDateTime fechaRecepcion;
    private OffsetDateTime fechaActualizacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBancoBatchId() {
        return bancoBatchId;
    }

    public void setBancoBatchId(Long bancoBatchId) {
        this.bancoBatchId = bancoBatchId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchDetailId() {
        return batchDetailId;
    }

    public void setBatchDetailId(String batchDetailId) {
        this.batchDetailId = batchDetailId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcionEstado() {
        return descripcionEstado;
    }

    public void setDescripcionEstado(String descripcionEstado) {
        this.descripcionEstado = descripcionEstado;
    }

    public String getTransactionIdDebito() {
        return transactionIdDebito;
    }

    public void setTransactionIdDebito(String transactionIdDebito) {
        this.transactionIdDebito = transactionIdDebito;
    }

    public String getTransactionIdCredito() {
        return transactionIdCredito;
    }

    public void setTransactionIdCredito(String transactionIdCredito) {
        this.transactionIdCredito = transactionIdCredito;
    }

    public OffsetDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(OffsetDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public OffsetDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
