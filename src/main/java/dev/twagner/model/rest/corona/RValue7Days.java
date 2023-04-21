package dev.twagner.model.rest.corona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RValue7Days {

    private final Double value;

    public RValue7Days(@JsonProperty("value") Double value) {
        this.value = value;
    }
}
