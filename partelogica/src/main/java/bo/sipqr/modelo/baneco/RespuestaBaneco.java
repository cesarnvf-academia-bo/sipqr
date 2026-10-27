package bo.sipqr.modelo.baneco;

import com.fasterxml.jackson.annotation.JsonAlias;

public class RespuestaBaneco {
    private Integer responseCode;

    @JsonAlias("Message")
    private String message;

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
