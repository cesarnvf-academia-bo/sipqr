package bo.sipqr.dto.baneco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DesencriptarSolicitud {
    @NotBlank(message = "El texto encriptado es obligatorio")
    @Size(max = 8192, message = "El texto encriptado no debe superar 8192 caracteres")
    private String textoEncriptado;

    public String getTextoEncriptado() {
        return textoEncriptado;
    }

    public void setTextoEncriptado(String textoEncriptado) {
        this.textoEncriptado = textoEncriptado;
    }
}
