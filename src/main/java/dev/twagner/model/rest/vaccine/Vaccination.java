package dev.twagner.model.rest.vaccine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vaccination {
    int biontech;
    int moderna;
    int astraZeneca;
    int jannsen;

    public Vaccination(@JsonProperty("biontech") int biontech,
                       @JsonProperty("moderna") int moderna,
                       @JsonProperty("astraZeneca") int astraZeneca,
                       @JsonProperty("janssen") int jannsen) {
        this.biontech = biontech;
        this.moderna = moderna;
        this.astraZeneca = astraZeneca;
        this.jannsen = jannsen;
    }
}
