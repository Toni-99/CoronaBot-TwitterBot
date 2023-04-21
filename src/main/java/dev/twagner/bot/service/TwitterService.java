package dev.twagner.bot.service;

import dev.twagner.model.TweetType;
import dev.twagner.http.ImageLoader;
import dev.twagner.model.rest.tweet.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.*;

@Component
public class TwitterService {

    private final Logger LOG = LoggerFactory.getLogger(TwitterService.class);
    private final Twitter twitter;
    private final ImageLoader imageLoader;

    public TwitterService(final Twitter twitter,
                          final ImageLoader imageLoader) {
        this.twitter = twitter;
        this.imageLoader = imageLoader;
    }

    public void sendTweet(Tweet tweet, TweetType tweetType) {
        StatusUpdate statusUpdate = new StatusUpdate(tweet.getTweet());
        long[] mediaIds = imageLoader.getImagesForTweet(tweetType);
        statusUpdate.setMediaIds(mediaIds);

        try {
            Status status = twitter.updateStatus(statusUpdate);
            LOG.info("[{}] Tweet wurde veröffentlicht, Tweet: {}", tweetType, status.getText());
        } catch (TwitterException e) {
            LOG.error("Fehler beim Senden des Tweets", e);
        }
    }
}
