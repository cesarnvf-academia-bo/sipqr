package bo.sipqr.modelo.baneco;

public class RespuestaGeneracionQr extends RespuestaBaneco {
    private String qrId;
    private String qrImage;

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public String getQrImage() {
        return qrImage;
    }

    public void setQrImage(String qrImage) {
        this.qrImage = qrImage;
    }
}
