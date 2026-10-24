package bo.sipqr.dto;

public class UsuarioRespuesta {
    private Long id;
    private String usuario;
    private Boolean estado;

    public UsuarioRespuesta() {
    }

    public UsuarioRespuesta(Long id, String usuario, Boolean estado) {
        this.id = id;
        this.usuario = usuario;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
