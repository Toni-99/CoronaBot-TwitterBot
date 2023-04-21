package dev.twagner.model.rest.vaccine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    private int administeredVaccinations, vaccinated, delta;
    private double quote;
    private Vaccination vaccination;
    private SecondVaccine secondVaccination;
    private Map<String, State> states;
    private double vaccinePercentDiff;
    private Booster booster;

    public Data(@JsonProperty("administeredVaccinations") int administeredVaccinations,
                @JsonProperty("vaccinated") int vaccinated,
                @JsonProperty("delta") int delta,
                @JsonProperty("quote") double quote,
                @JsonProperty("vaccination") Vaccination vaccination,
                @JsonProperty("secondVaccination") SecondVaccine secondVaccine,
                @JsonProperty("states") Map<String, State> states,
                @JsonProperty("boosterVaccination") Booster booster) {
        this.administeredVaccinations = administeredVaccinations;
        this.vaccinated = vaccinated;
        this.delta = delta;
        this.quote = quote;
        this.vaccination = vaccination;
        this.secondVaccination = secondVaccine;
        this.states = states;
        this.booster = booster;
    }

}
