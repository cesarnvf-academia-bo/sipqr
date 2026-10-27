package bo.sipqr.modelo.baneco;

public class RespuestaCargaPlanilla extends RespuestaBaneco {
    private Long bankBatchId;

    public Long getBankBatchId() {
        return bankBatchId;
    }

    public void setBankBatchId(Long bankBatchId) {
        this.bankBatchId = bankBatchId;
    }
}
