package bo.sipqr.modelo.baneco;

public class RespuestaCallbackBaneco {
    private Integer responseCode;
    private String message;

    public RespuestaCallbackBaneco() {
        this.responseCode = 0;
        this.message = "";
    }

    public RespuestaCallbackBaneco(Integer responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
