package bo.sipqr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AccesoSolicitud {
    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 100, message = "El usuario no debe superar 100 caracteres")
    private String usuario;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 72, message = "La clave no debe superar 72 caracteres")
    private String clave;

    public AccesoSolicitud() {
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
}
