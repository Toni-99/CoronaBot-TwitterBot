package dev.twagner.model.rest.vaccine;

import lombok.Data;

@Data
public class Tweet {
    private String tweet;

    public Tweet() {}

    public Tweet(String tweet) {
        this.tweet = tweet;
    }
}
