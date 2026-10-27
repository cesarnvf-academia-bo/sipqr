package bo.sipqr.dto;

public class AccesoRespuesta {
    private String token;
    private String tipo;
    private String usuario;
    private String rol;
    private long expiraEn;

    public AccesoRespuesta() {
    }

    public AccesoRespuesta(String token, String tipo, String usuario, String rol, long expiraEn) {
        this.token = token;
        this.tipo = tipo;
        this.usuario = usuario;
        this.rol = rol;
        this.expiraEn = expiraEn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public long getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(long expiraEn) {
        this.expiraEn = expiraEn;
    }
}
