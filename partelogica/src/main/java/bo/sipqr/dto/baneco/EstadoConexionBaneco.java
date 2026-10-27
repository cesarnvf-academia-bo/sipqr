package bo.sipqr.dto.baneco;

import java.time.OffsetDateTime;

public class EstadoConexionBaneco {
    private boolean conectado;
    private String mensaje;
    private OffsetDateTime fecha;

    public EstadoConexionBaneco() {
    }

    public EstadoConexionBaneco(boolean conectado, String mensaje) {
        this.conectado = conectado;
        this.mensaje = mensaje;
        this.fecha = OffsetDateTime.now();
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }
}
