package dev.twagner.bot.service;

import dev.twagner.model.TweetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {

    private final Logger LOG = LoggerFactory.getLogger(TelegramService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final String authToken;
    private final String baseUrl;

    public TelegramService(@Value("${telegram.authToken}") final String authToken,
                           @Value("${telegram.baseUrl}") final String baseUrl) {
        this.authToken = authToken;
        this.baseUrl = baseUrl;
    }

    public void sendTelegramUpdate(TweetType tweetType) {
        HttpHeaders headers = new HttpHeaders();
        var request = new HttpEntity<>(headers);
        headers.add("authToken", authToken);

        LOG.info("Sende Telegram Update für TweetType = {}", tweetType);
        restTemplate.postForEntity(baseUrl.concat(tweetType.getType()), request, Void.class);
    }
}