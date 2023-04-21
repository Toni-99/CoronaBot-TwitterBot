package dev.twagner.model.rest.corona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RValue {

    private final Double rValue;
    private final RValue7Days rValue7Days;

    public RValue(@JsonProperty("value") Double rValue,
                  @JsonProperty("rValue7Days") RValue7Days rValue7Days) {
        this.rValue = rValue;
        this.rValue7Days = rValue7Days;
    }
}
