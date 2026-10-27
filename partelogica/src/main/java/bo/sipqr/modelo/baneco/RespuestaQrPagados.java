package bo.sipqr.modelo.baneco;

import java.util.ArrayList;
import java.util.List;

public class RespuestaQrPagados extends RespuestaBaneco {
    private List<PaymentQR> paymentList = new ArrayList<PaymentQR>();

    public List<PaymentQR> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<PaymentQR> paymentList) {
        this.paymentList = paymentList;
    }
}
