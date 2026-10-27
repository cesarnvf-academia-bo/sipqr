package bo.sipqr.repositorio.mapeador;

import bo.sipqr.modelo.PagoQrDetalle;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PagoQrDetalleMapeador {
    int insertarSiNoExiste(PagoQrDetalle detalle);

    List<PagoQrDetalle> encontrarPorPagoQrId(@Param("pagoQrId") Long pagoQrId);

    BigDecimal sumarImportes(@Param("pagoQrId") Long pagoQrId);
}
