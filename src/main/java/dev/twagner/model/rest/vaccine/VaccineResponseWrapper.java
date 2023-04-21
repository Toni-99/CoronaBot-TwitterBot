package dev.twagner.model.rest.vaccine;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.twagner.model.persistence.VaccineDAO;

import java.time.LocalDate;

@lombok.Data
public class VaccineResponseWrapper {

    private Data data;
    private Meta meta;

    public VaccineResponseWrapper(@JsonProperty("data") Data data,
                                  @JsonProperty("meta") Meta meta) {
        this.data = data;
        this.meta = meta;
    }

    public VaccineDAO toVaccineDAO() {
        return new VaccineDAO(
                LocalDate.now(),
                data.getAdministeredVaccinations(),
                data.getDelta(),
                data.getQuote() * 100,
                data.getVaccinePercentDiff(),
                data.getSecondVaccination().getVaccinated(),
                data.getSecondVaccination().getDelta(),
                data.getSecondVaccination().getQuote() * 100,
                data.getSecondVaccination().getVaccinePercentDiff(),
                data.getBooster().getDelta()
        );
    }

}
