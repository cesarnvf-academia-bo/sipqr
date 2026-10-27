package bo.sipqr.repositorio.mapeador.baneco;

import bo.sipqr.modelo.baneco.BanecoDetalleLote;
import bo.sipqr.modelo.baneco.BanecoOperacion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BanecoOperacionMapeador {
    BanecoOperacion buscarOperacion(@Param("tipo") String tipo, @Param("transactionId") String transactionId);

    int insertarOperacion(BanecoOperacion operacion);

    int actualizarOperacion(BanecoOperacion operacion);

    int insertarNotificacion(@Param("tipo") String tipo,
                             @Param("claveEvento") String claveEvento,
                             @Param("hashPayload") String hashPayload,
                             @Param("estado") String estado);

    int guardarDetalleLote(BanecoDetalleLote detalle);
}
