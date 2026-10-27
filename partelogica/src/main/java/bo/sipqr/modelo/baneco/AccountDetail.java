package bo.sipqr.modelo.baneco;

import java.math.BigDecimal;

public class AccountDetail {
    private Long transactionId;
    private String date;
    private String time;
    private Long documentNumber;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private String clienteNote;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Long documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClienteNote() {
        return clienteNote;
    }

    public void setClienteNote(String clienteNote) {
        this.clienteNote = clienteNote;
    }
}
