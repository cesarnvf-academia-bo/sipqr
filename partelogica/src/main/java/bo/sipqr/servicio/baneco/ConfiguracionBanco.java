package bo.sipqr.servicio.baneco;

import java.time.OffsetDateTime;

public record ConfiguracionBanco(
    String userName,
    String password,
    String keyencript,
    String account,
    String url,
    OffsetDateTime fechaActualizacion
) {
}
