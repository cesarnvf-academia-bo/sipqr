package bo.sipqr.dto.baneco;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class GenerarQrSolicitud {
    @NotBlank(message = "El identificador de transacción es obligatorio")
    @Size(max = 150, message = "El identificador de transacción no debe superar 150 caracteres")
    private String transactionId;

    @NotBlank(message = "La moneda es obligatoria")
    private String currency;

    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a cero")
    @Digits(integer = 15, fraction = 2, message = "El importe debe tener como máximo dos decimales")
    private BigDecimal amount;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    private String dueDate;

    @NotNull(message = "El indicador de uso único es obligatorio")
    private Boolean singleUse;

    @NotNull(message = "El indicador de modificación de importe es obligatorio")
    private Boolean modifyAmount;

    @Size(max = 500, message = "La descripción no debe superar 500 caracteres")
    private String description;

    @Size(max = 5, message = "El código de sucursal no debe superar 5 caracteres")
    private String branchCode;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
}
