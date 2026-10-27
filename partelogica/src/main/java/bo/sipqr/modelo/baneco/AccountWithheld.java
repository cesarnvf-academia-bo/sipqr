package bo.sipqr.modelo.baneco;

import java.math.BigDecimal;

public class AccountWithheld {
    private Long transactionId;
    private String date;
    private String time;
    private BigDecimal amount;
    private String description;
    private String instruction;
    private String demanding;
    private String judge;
    private String piet;

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

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDemanding() {
        return demanding;
    }

    public void setDemanding(String demanding) {
        this.demanding = demanding;
    }

    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }

    public String getPiet() {
        return piet;
    }

    public void setPiet(String piet) {
        this.piet = piet;
    }
}
