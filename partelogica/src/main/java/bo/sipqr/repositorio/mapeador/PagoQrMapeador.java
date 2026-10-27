package bo.sipqr.repositorio.mapeador;

import bo.sipqr.modelo.PagoQr;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PagoQrMapeador {
    List<PagoQr> encontrarTodos(@Param("buscar") String buscar, @Param("estado") String estado,
                                @Param("limite") int limite, @Param("desplazamiento") int desplazamiento);

    long contar(@Param("buscar") String buscar, @Param("estado") String estado);

    PagoQr encontrarPorId(@Param("id") Long id);

    int insertar(PagoQr pagoQr);

    int marcarGenerando(@Param("id") Long id);

    int actualizarGeneracion(PagoQr pagoQr);

    int actualizarVerificacion(PagoQr pagoQr);

    int actualizarError(@Param("id") Long id, @Param("qrId") String qrId,
                        @Param("responseCode") String responseCode,
                        @Param("responseMessage") String responseMessage,
                        @Param("resultadoIncierto") boolean resultadoIncierto);

    int actualizarAnulacion(PagoQr pagoQr);

    int borrarRegistrado(@Param("id") Long id);
}
