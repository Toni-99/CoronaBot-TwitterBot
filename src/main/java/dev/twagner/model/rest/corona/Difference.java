package dev.twagner.model.rest.corona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Difference {
    private final Integer cases;
    private final Integer recovered;
    private final Integer deaths;

    public Difference(@JsonProperty("cases") Integer cases,
                      @JsonProperty("recovered") Integer recovered,
                      @JsonProperty("deaths") Integer deaths) {
        this.cases = cases;
        this.recovered = recovered;
        this.deaths = deaths;
    }
}
