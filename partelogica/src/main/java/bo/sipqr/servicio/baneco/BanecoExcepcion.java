package bo.sipqr.servicio.baneco;

public class BanecoExcepcion extends RuntimeException {
    private final TipoErrorBaneco tipo;
    private final Integer codigoBanco;
    private final Integer estadoHttp;

    public BanecoExcepcion(TipoErrorBaneco tipo, String mensaje) {
        this(tipo, mensaje, null, null, null);
    }

    public BanecoExcepcion(TipoErrorBaneco tipo, String mensaje, Throwable causa) {
        this(tipo, mensaje, null, null, causa);
    }

    public BanecoExcepcion(TipoErrorBaneco tipo, String mensaje, Integer codigoBanco, Integer estadoHttp) {
        this(tipo, mensaje, codigoBanco, estadoHttp, null);
    }

    public BanecoExcepcion(TipoErrorBaneco tipo, String mensaje, Integer codigoBanco, Integer estadoHttp, Throwable causa) {
        super(mensaje, causa);
        this.tipo = tipo;
        this.codigoBanco = codigoBanco;
        this.estadoHttp = estadoHttp;
    }

    public TipoErrorBaneco getTipo() {
        return tipo;
    }

    public Integer getCodigoBanco() {
        return codigoBanco;
    }

    public Integer getEstadoHttp() {
        return estadoHttp;
    }
}
