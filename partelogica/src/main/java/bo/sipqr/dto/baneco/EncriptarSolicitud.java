package bo.sipqr.dto.baneco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EncriptarSolicitud {
    @NotBlank(message = "El texto es obligatorio")
    @Size(max = 4096, message = "El texto no debe superar 4096 caracteres")
    private String texto;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
