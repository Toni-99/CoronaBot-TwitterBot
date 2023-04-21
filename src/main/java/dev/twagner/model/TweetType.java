package dev.twagner.model;

import lombok.Getter;

@Getter
public enum TweetType {

    STATISTICS("statistics"),
    CONCLUSION("conclusion"),
    HOSPITALIZATION("hospitalization"),
    VACCINE("vaccination");

    private final String type;

    TweetType(String type) {
        this.type = type;
    }
}
