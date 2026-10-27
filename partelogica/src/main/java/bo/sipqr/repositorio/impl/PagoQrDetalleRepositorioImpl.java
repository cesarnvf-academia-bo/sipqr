package bo.sipqr.repositorio.impl;

import bo.sipqr.modelo.PagoQrDetalle;
import bo.sipqr.repositorio.PagoQrDetalleRepositorio;
import bo.sipqr.repositorio.mapeador.PagoQrDetalleMapeador;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PagoQrDetalleRepositorioImpl implements PagoQrDetalleRepositorio {
    private final PagoQrDetalleMapeador pagoQrDetalleMapeador;

    public PagoQrDetalleRepositorioImpl(PagoQrDetalleMapeador pagoQrDetalleMapeador) {
        this.pagoQrDetalleMapeador = pagoQrDetalleMapeador;
    }

    @Override
    public int insertarSiNoExiste(PagoQrDetalle detalle) {
        return pagoQrDetalleMapeador.insertarSiNoExiste(detalle);
    }

    @Override
    public List<PagoQrDetalle> encontrarPorPagoQrId(Long pagoQrId) {
        return pagoQrDetalleMapeador.encontrarPorPagoQrId(pagoQrId);
    }

    @Override
    public BigDecimal sumarImportes(Long pagoQrId) {
        return pagoQrDetalleMapeador.sumarImportes(pagoQrId);
    }
}
