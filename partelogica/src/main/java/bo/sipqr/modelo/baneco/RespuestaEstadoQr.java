package bo.sipqr.modelo.baneco;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

public class RespuestaEstadoQr extends RespuestaBaneco {
    @JsonAlias({"statusQRCode", "statusQrCode"})
    private Integer statusQrCode;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<PaymentQR> payment;

    public Integer getStatusQrCode() {
        return statusQrCode;
    }

    public void setStatusQrCode(Integer statusQrCode) {
        this.statusQrCode = statusQrCode;
    }

    public List<PaymentQR> getPayment() {
        return payment;
    }

    public void setPayment(List<PaymentQR> payment) {
        this.payment = payment;
    }
}
