package bo.sipqr.dto;

import java.util.List;

public class PaginaRespuesta<T> {
    private List<T> datos;
    private int pagina;
    private int limite;
    private long totalRegistros;
    private int totalPaginas;

    public PaginaRespuesta() {
    }

    public PaginaRespuesta(List<T> datos, int pagina, int limite, long totalRegistros) {
        this.datos = datos;
        this.pagina = pagina;
        this.limite = limite;
        this.totalRegistros = totalRegistros;
        this.totalPaginas = limite <= 0 ? 0 : (int) Math.ceil((double) totalRegistros / (double) limite);
    }

    public List<T> getDatos() {
        return datos;
    }

    public void setDatos(List<T> datos) {
        this.datos = datos;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(long totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }
}
