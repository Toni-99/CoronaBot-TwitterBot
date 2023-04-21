package dev.twagner.model.rest.vaccine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {
    private String name;
    private int vaccinated;

    private double quote;

    private int delta;
    private Vaccination vaccination;
    private SecondVaccine secondVaccine;

    public State(@JsonProperty("name") String name,
                 @JsonProperty("vaccinated") int vaccinated,
                 @JsonProperty("vaccination") Vaccination vaccination,
                 @JsonProperty("secondVaccination") SecondVaccine secondVaccine,
                 @JsonProperty("quote") double quote,
                 @JsonProperty("delta") int delta) {
        this.name = name;
        this.vaccinated = vaccinated;
        this.vaccination = vaccination;
        this.secondVaccine = secondVaccine;
        this.quote = quote;
        this.delta = delta;
    }
}
