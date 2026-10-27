package bo.sipqr.modelo.baneco;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AMLData {
    @JsonProperty("AMLSource")
    private String AMLSource;

    @JsonProperty("AMLDestination")
    private String AMLDestination;

    public String getAMLSource() {
        return AMLSource;
    }

    public void setAMLSource(String AMLSource) {
        this.AMLSource = AMLSource;
    }

    public String getAMLDestination() {
        return AMLDestination;
    }

    public void setAMLDestination(String AMLDestination) {
        this.AMLDestination = AMLDestination;
    }
}
