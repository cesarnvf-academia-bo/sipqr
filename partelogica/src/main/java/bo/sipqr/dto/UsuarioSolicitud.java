package bo.sipqr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioSolicitud {
    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 4, max = 100, message = "El usuario debe tener entre 4 y 100 caracteres")
    private String usuario;

    @NotBlank(message = "La clave es obligatoria")
    @Size(min = 12, max = 72, message = "La clave debe tener entre 12 y 72 caracteres")
    private String clave;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    public UsuarioSolicitud() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
