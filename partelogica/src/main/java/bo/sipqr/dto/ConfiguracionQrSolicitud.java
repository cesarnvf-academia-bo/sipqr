package bo.sipqr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConfiguracionQrSolicitud {
    @NotBlank(message = "El usuario bancario es obligatorio")
    @Size(max = 150, message = "El usuario bancario no debe superar 150 caracteres")
    private String userName;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 4096, message = "La contraseña no debe superar 4096 caracteres")
    private String password;

    @NotBlank(message = "La clave de encriptación es obligatoria")
    @Size(max = 4096, message = "La clave de encriptación no debe superar 4096 caracteres")
    private String keyencript;

    @NotBlank(message = "La cuenta es obligatoria")
    @Size(max = 4096, message = "La cuenta no debe superar 4096 caracteres")
    private String account;

    @NotBlank(message = "La URL es obligatoria")
    @Size(max = 2048, message = "La URL no debe superar 2048 caracteres")
    private String url;

    public ConfiguracionQrSolicitud() {
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
}
