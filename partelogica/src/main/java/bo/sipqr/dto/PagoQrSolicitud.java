package bo.sipqr.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoQrSolicitud {
    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a cero")
    @Digits(integer = 15, fraction = 2, message = "El importe debe tener como máximo dos decimales")
    private BigDecimal amount;

    @NotBlank(message = "La moneda es obligatoria")
    @Pattern(regexp = "(?i)BOB|USD", message = "La moneda debe ser BOB o USD")
    private String currency;

    @Size(max = 500, message = "La descripción no debe superar 500 caracteres")
    private String description;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @FutureOrPresent(message = "La fecha de vencimiento no puede ser anterior a hoy")
    private LocalDate dueDate;

    @NotNull(message = "El indicador de uso único es obligatorio")
    private Boolean singleUse;

    @NotNull(message = "El indicador de modificación de importe es obligatorio")
    private Boolean modifyAmount;

    @Size(max = 5, message = "El código de sucursal no debe superar 5 caracteres")
    private String branchCode;

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
}
