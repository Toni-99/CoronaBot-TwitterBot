package dev.twagner.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
@Getter
public class TwitterConfiguration {

    private final String consumerKey;
    private final String consumerKeySecret;
    private final String OAuthAccessToken;
    private final String OAuthAccessTokenSecret;

    public TwitterConfiguration(@Value("${twitter.consumerKey}") final String consumerKey,
                                @Value("${twitter.consumerKeySecret}")final String consumerKeySecret,
                                @Value("${twitter.OAuthAccessToken}") final String OAuthAccessToken,
                                @Value("${twitter.OAuthAccessTokenSecret}") final String OAuthAccessTokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerKeySecret = consumerKeySecret;
        this.OAuthAccessToken = OAuthAccessToken;
        this.OAuthAccessTokenSecret = OAuthAccessTokenSecret;
    }

    @Bean
    public Twitter getTwitterInstance() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerKeySecret)
                .setOAuthAccessToken(OAuthAccessToken)
                .setOAuthAccessTokenSecret(OAuthAccessTokenSecret);

        return new TwitterFactory(configurationBuilder.build()).getInstance();
    }
}
