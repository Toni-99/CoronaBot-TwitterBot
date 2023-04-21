package dev.twagner.model.rest.corona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.twagner.model.persistence.CoronaDAO;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Corona {

    private final Integer cases;
    private final Integer deaths;
    private final Integer recovered;
    private final Double weekIncidence;
    private final Difference difference;
    private final RValue rValue;

    public Corona(@JsonProperty("cases") Integer cases,
                  @JsonProperty("deaths") Integer deaths,
                  @JsonProperty("recovered") Integer recovered,
                  @JsonProperty("delta") Difference difference,
                  @JsonProperty("weekIncidence") Double weekIncidence,
                  @JsonProperty("r") RValue rValue) {
        this.cases = cases;
        this.deaths = deaths;
        this.recovered = recovered;
        this.difference = difference;
        this.weekIncidence = weekIncidence;
        this.rValue = rValue;
    }

    public CoronaDAO toCoronaDAO() {
        return new CoronaDAO(
                LocalDate.now(),
                cases,
                deaths,
                recovered,
                difference.getCases(),
                difference.getDeaths(),
                difference.getRecovered(),
                weekIncidence,
                rValue.getRValue7Days().getValue()
        );
    }
}
