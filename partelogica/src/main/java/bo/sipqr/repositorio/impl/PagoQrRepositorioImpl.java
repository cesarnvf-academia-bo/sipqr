package bo.sipqr.repositorio.impl;

import bo.sipqr.modelo.PagoQr;
import bo.sipqr.repositorio.PagoQrRepositorio;
import bo.sipqr.repositorio.mapeador.PagoQrMapeador;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PagoQrRepositorioImpl implements PagoQrRepositorio {
    private final PagoQrMapeador pagoQrMapeador;

    public PagoQrRepositorioImpl(PagoQrMapeador pagoQrMapeador) {
        this.pagoQrMapeador = pagoQrMapeador;
    }

    @Override
    public List<PagoQr> encontrarTodos(String buscar, String estado, int limite, int desplazamiento) {
        return pagoQrMapeador.encontrarTodos(buscar, estado, limite, desplazamiento);
    }

    @Override
    public long contar(String buscar, String estado) {
        return pagoQrMapeador.contar(buscar, estado);
    }

    @Override
    public Optional<PagoQr> encontrarPorId(Long id) {
        return Optional.ofNullable(pagoQrMapeador.encontrarPorId(id));
    }

    @Override
    public int insertar(PagoQr pagoQr) {
        return pagoQrMapeador.insertar(pagoQr);
    }

    @Override
    public int marcarGenerando(Long id) {
        return pagoQrMapeador.marcarGenerando(id);
    }

    @Override
    public int actualizarGeneracion(PagoQr pagoQr) {
        return pagoQrMapeador.actualizarGeneracion(pagoQr);
    }

    @Override
    public int actualizarVerificacion(PagoQr pagoQr) {
        return pagoQrMapeador.actualizarVerificacion(pagoQr);
    }

    @Override
    public int actualizarError(Long id, String qrId, String responseCode, String responseMessage,
                               boolean resultadoIncierto) {
        return pagoQrMapeador.actualizarError(id, qrId, responseCode, responseMessage, resultadoIncierto);
    }

    @Override
    public int actualizarAnulacion(PagoQr pagoQr) {
        return pagoQrMapeador.actualizarAnulacion(pagoQr);
    }

    @Override
    public int borrarRegistrado(Long id) {
        return pagoQrMapeador.borrarRegistrado(id);
    }
}
