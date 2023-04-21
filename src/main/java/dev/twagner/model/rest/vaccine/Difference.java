package dev.twagner.model.rest.vaccine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Difference {
    private int cases, recovered, deaths;

    public Difference(@JsonProperty("cases") int cases,
                      @JsonProperty("recovered") int recovered,
                      @JsonProperty("deaths") int deaths) {
        this.cases = cases;
        this.recovered = recovered;
        this.deaths = deaths;
    }
}
