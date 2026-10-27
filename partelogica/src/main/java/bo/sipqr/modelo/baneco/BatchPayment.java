package bo.sipqr.modelo.baneco;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class BatchPayment {
    private String batchDetailId;
    private BigDecimal amount;
    private String accountCode;
    private String accountTypeCode;
    private String bankCode;
    private String beneficiaryName;
    private String beneficaryDocId;
    private String beneficiaryPhone;
    private String beneficiaryEmail;
    private String note;

    @JsonProperty("AMLData")
    private AMLData amlData;

    public String getBatchDetailId() {
        return batchDetailId;
    }

    public void setBatchDetailId(String batchDetailId) {
        this.batchDetailId = batchDetailId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficaryDocId() {
        return beneficaryDocId;
    }

    public void setBeneficaryDocId(String beneficaryDocId) {
        this.beneficaryDocId = beneficaryDocId;
    }

    public String getBeneficiaryPhone() {
        return beneficiaryPhone;
    }

    public void setBeneficiaryPhone(String beneficiaryPhone) {
        this.beneficiaryPhone = beneficiaryPhone;
    }

    public String getBeneficiaryEmail() {
        return beneficiaryEmail;
    }

    public void setBeneficiaryEmail(String beneficiaryEmail) {
        this.beneficiaryEmail = beneficiaryEmail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AMLData getAmlData() {
        return amlData;
    }

    public void setAmlData(AMLData amlData) {
        this.amlData = amlData;
    }
}
