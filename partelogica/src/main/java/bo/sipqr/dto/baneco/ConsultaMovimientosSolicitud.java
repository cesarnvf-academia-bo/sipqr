package bo.sipqr.dto.baneco;

import jakarta.validation.constraints.NotBlank;

public class ConsultaMovimientosSolicitud {
    @NotBlank(message = "La fecha inicial es obligatoria")
    private String startDate;

    @NotBlank(message = "La fecha final es obligatoria")
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
