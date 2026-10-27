package bo.sipqr.repositorio;

import bo.sipqr.modelo.PagoQrDetalle;
import java.math.BigDecimal;
import java.util.List;

public interface PagoQrDetalleRepositorio {
    int insertarSiNoExiste(PagoQrDetalle detalle);

    List<PagoQrDetalle> encontrarPorPagoQrId(Long pagoQrId);

    BigDecimal sumarImportes(Long pagoQrId);
}
