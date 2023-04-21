package dev.twagner.model.rest.vaccine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecondVaccine {
    private int vaccinated, delta;
    private double quote;
    private Vaccination vaccination;
    private double vaccinePercentDiff;

    public SecondVaccine(@JsonProperty("vaccinated") int vaccinated,
                         @JsonProperty("delta") int delta,
                         @JsonProperty("quote") double quote,
                         @JsonProperty("vaccination") Vaccination vaccination) {
        this.vaccinated = vaccinated;
        this.delta = delta;
        this.quote = quote;
        this.vaccination = vaccination;
    }

}
