package bo.sipqr.modelo.baneco;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LotePagoSolicitud {
    private String batchId;
    private String type;
    private String descripction;
    private Boolean detailedDebit;
    private String accountCode;
    private String batchCurrency;
    private BigDecimal batchAmount;

    @JsonProperty("AMLData")
    private AMLData amlData;

    private Integer paymentCount;
    private List<BatchPayment> paymentList = new ArrayList<BatchPayment>();

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescripction() {
        return descripction;
    }

    public void setDescripction(String descripction) {
        this.descripction = descripction;
    }

    public Boolean getDetailedDebit() {
        return detailedDebit;
    }

    public void setDetailedDebit(Boolean detailedDebit) {
        this.detailedDebit = detailedDebit;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getBatchCurrency() {
        return batchCurrency;
    }

    public void setBatchCurrency(String batchCurrency) {
        this.batchCurrency = batchCurrency;
    }

    public BigDecimal getBatchAmount() {
        return batchAmount;
    }

    public void setBatchAmount(BigDecimal batchAmount) {
        this.batchAmount = batchAmount;
    }

    public AMLData getAmlData() {
        return amlData;
    }

    public void setAmlData(AMLData amlData) {
        this.amlData = amlData;
    }

    public Integer getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(Integer paymentCount) {
        this.paymentCount = paymentCount;
    }

    public List<BatchPayment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<BatchPayment> paymentList) {
        this.paymentList = paymentList;
    }
}
