package bo.sipqr.modelo.baneco;

public class NotificacionLoteSolicitud {
    private Long bankBatchId;
    private String batchId;
    private String batchDetailId;
    private String status;
    private String descriptionStatus;
    private Long transactionIdDebit;
    private String transactionIdCredit;

    public Long getBankBatchId() {
        return bankBatchId;
    }

    public void setBankBatchId(Long bankBatchId) {
        this.bankBatchId = bankBatchId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescriptionStatus() {
        return descriptionStatus;
    }

    public void setDescriptionStatus(String descriptionStatus) {
        this.descriptionStatus = descriptionStatus;
    }

    public Long getTransactionIdDebit() {
        return transactionIdDebit;
    }

    public void setTransactionIdDebit(Long transactionIdDebit) {
        this.transactionIdDebit = transactionIdDebit;
    }

    public String getTransactionIdCredit() {
        return transactionIdCredit;
    }

    public void setTransactionIdCredit(String transactionIdCredit) {
        this.transactionIdCredit = transactionIdCredit;
    }
}
