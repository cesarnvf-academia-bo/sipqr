package bo.sipqr.modelo;

import java.time.OffsetDateTime;

public class ConfiguracionQr {
    private Short id;
    private String userName;
    private String password;
    private String keyencript;
    private String account;
    private String url;
    private OffsetDateTime fechaActualizacion;

    public ConfiguracionQr() {
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeyencript() {
        return keyencript;
    }

    public void setKeyencript(String keyencript) {
        this.keyencript = keyencript;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public OffsetDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
