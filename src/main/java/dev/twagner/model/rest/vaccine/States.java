package dev.twagner.model.rest.vaccine;

import lombok.Data;

import java.util.Map;

@Data
public class States {
    private Map<String, State> states;

    public States(Map<String, State> states) {
        this.states = states;
    }

}
