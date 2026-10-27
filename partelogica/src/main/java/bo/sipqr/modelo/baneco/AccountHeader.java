package bo.sipqr.modelo.baneco;

import java.math.BigDecimal;

public class AccountHeader {
    private String accountCode;
    private String accountTypeCode;
    private String productName;
    private String status;
    private String currency;
    private BigDecimal balance;
    private BigDecimal balanceReserved;
    private BigDecimal balanceRetained;
    private BigDecimal balanceAvailable;

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalanceReserved() {
        return balanceReserved;
    }

    public void setBalanceReserved(BigDecimal balanceReserved) {
        this.balanceReserved = balanceReserved;
    }

    public BigDecimal getBalanceRetained() {
        return balanceRetained;
    }

    public void setBalanceRetained(BigDecimal balanceRetained) {
        this.balanceRetained = balanceRetained;
    }

    public BigDecimal getBalanceAvailable() {
        return balanceAvailable;
    }

    public void setBalanceAvailable(BigDecimal balanceAvailable) {
        this.balanceAvailable = balanceAvailable;
    }
}
