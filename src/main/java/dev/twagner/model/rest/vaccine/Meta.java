package dev.twagner.model.rest.vaccine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {

    String source = "Robert Koch-Institut", contact = "Toni Wagner (kontakt@twagner.dev)", lastUpdate, lastCheckedForUpdate;

    public Meta(@JsonProperty("lastUpdate") String lastUpdate,
                @JsonProperty("lastCheckedForUpdate") String lastCheckedForUpdate) {
        this.lastUpdate = lastUpdate;
        this.lastCheckedForUpdate = lastCheckedForUpdate;
    }
}