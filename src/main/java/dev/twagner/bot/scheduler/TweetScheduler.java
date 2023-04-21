package dev.twagner.bot.scheduler;

import dev.twagner.bot.service.TelegramService;
import dev.twagner.bot.service.TwitterService;
import dev.twagner.bot.util.TweetConverter;
import dev.twagner.http.ObjectLoader;
import dev.twagner.model.TweetType;
import dev.twagner.model.rest.corona.Corona;
import dev.twagner.model.rest.hospitalization.HospitalizationData;
import dev.twagner.model.rest.tweet.Tweet;
import dev.twagner.model.rest.vaccine.VaccineResponseWrapper;
import dev.twagner.repository.CoronaRepository;
import dev.twagner.repository.HospitalizationRepository;
import dev.twagner.repository.VaccineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TweetScheduler {

    private final ObjectLoader objectLoader;
    private final TwitterService twitterService;
    private final CoronaRepository coronaRepository;
    private final HospitalizationRepository hospitalizationRepository;
    private final TweetConverter tweetConverter;
    private final TelegramService telegramService;
    private final VaccineRepository vaccineRepository;

    public TweetScheduler(final ObjectLoader objectLoader,
                          final TwitterService twitterService,
                          final CoronaRepository coronaRepository,
                          final HospitalizationRepository hospitalizationRepository,
                          final TweetConverter tweetConverter,
                          final TelegramService telegramService,
                          final VaccineRepository vaccineRepository) {
        this.objectLoader = objectLoader;
        this.twitterService = twitterService;
        this.coronaRepository = coronaRepository;
        this.hospitalizationRepository = hospitalizationRepository;
        this.tweetConverter = tweetConverter;
        this.telegramService = telegramService;
        this.vaccineRepository = vaccineRepository;
    }

   @Scheduled(cron = "0 45 7 * * MON-SUN")
   public void statisticTweetScheduler() {
        Optional<Corona> optCorona = objectLoader.getCoronaStats();
        if(optCorona.isEmpty()) {
            return;
        }

        Corona corona = optCorona.get();
        Tweet tweet = tweetConverter.createStatisticsTweet(corona);

        coronaRepository.save(corona.toCoronaDAO());
        twitterService.sendTweet(tweet, TweetType.STATISTICS);
        telegramService.sendTelegramUpdate(TweetType.STATISTICS);
    }

    @Scheduled(cron = "0 0 8 * * SUN")
    public void conclusionTweetScheduler() {
        Tweet tweet = tweetConverter.createConclusionTweet();

        twitterService.sendTweet(tweet, TweetType.CONCLUSION);
        telegramService.sendTelegramUpdate(TweetType.CONCLUSION);
    }

   @Scheduled(cron = "0 30 8 * * MON-SUN")
   public void hospitalizationTweetScheduler() {
        Optional<HospitalizationData> optHospitalizationData = objectLoader.getHospitalizationData();
        if(optHospitalizationData.isEmpty()) {
            return;
        }

        HospitalizationData hospitalizationData = optHospitalizationData.get();
        Tweet tweet = tweetConverter.createHospitalizationTweet(hospitalizationData);

        hospitalizationRepository.save(hospitalizationData.toHospitalizationDAO());
        twitterService.sendTweet(tweet, TweetType.HOSPITALIZATION);
   }

    // Thanks to the over 9000 IQ move, the RKI is posting new Vaccination Data, we need to
    // fetch them every 30 minutes as they post them over the whole day.
    // Additionally: They only post the newest data from monday to friday. If they feel like,
    // they'll also do it on saturdays. But that only happens some times a month and you dont know when 🙃🔫
    @Scheduled(cron = "0 */30 * * * MON-FRI")
    public void vaccineTweetScheduler() {
        Optional<VaccineResponseWrapper> optVaccine = objectLoader.getVaccineStats();
        if(optVaccine.isEmpty() || !objectLoader.checkForUpdates(optVaccine.get())) {
            return;
        }

        VaccineResponseWrapper vaccine = optVaccine.get();
        Tweet tweet = tweetConverter.createVaccineTweet(vaccine);

        vaccineRepository.save(vaccine.toVaccineDAO());
        twitterService.sendTweet(tweet, TweetType.VACCINE);
        telegramService.sendTelegramUpdate(TweetType.VACCINE);
    }

}
