package bo.sipqr.repositorio;

import bo.sipqr.modelo.PagoQr;
import java.util.List;
import java.util.Optional;

public interface PagoQrRepositorio {
    List<PagoQr> encontrarTodos(String buscar, String estado, int limite, int desplazamiento);

    long contar(String buscar, String estado);

    Optional<PagoQr> encontrarPorId(Long id);

    int insertar(PagoQr pagoQr);

    int marcarGenerando(Long id);

    int actualizarGeneracion(PagoQr pagoQr);

    int actualizarVerificacion(PagoQr pagoQr);

    int actualizarError(Long id, String qrId, String responseCode, String responseMessage,
                        boolean resultadoIncierto);

    int actualizarAnulacion(PagoQr pagoQr);

    int borrarRegistrado(Long id);
}
