package dev.twagner.bot.util;

import dev.twagner.model.persistence.CoronaDAO;
import dev.twagner.model.persistence.HospitalizationDAO;
import dev.twagner.model.persistence.VaccineDAO;
import dev.twagner.model.rest.hospitalization.HospitalizationData;
import dev.twagner.model.rest.corona.Corona;
import dev.twagner.model.rest.tweet.Tweet;
import dev.twagner.model.rest.vaccine.VaccineResponseWrapper;
import dev.twagner.repository.CoronaRepository;
import dev.twagner.repository.HospitalizationRepository;
import dev.twagner.repository.VaccineRepository;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/*
    Please don't blame me for this class. I was too lazy to have a nice String formatting #RKIStyle
    (And yeah I know about StringBuilders but project is over sooooooo)
 */

@Component
public class TweetConverter {

    private final CoronaRepository coronaRepository;
    private final VaccineRepository vaccineRepository;
    private final HospitalizationRepository hospitalizationRepository;

    private final NumberFormat numberFormatter = NumberFormat.getInstance(Locale.GERMAN);
    private final DecimalFormat decimalFormatter = new DecimalFormat("###.##");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public TweetConverter(final CoronaRepository coronaRepository,
                          final VaccineRepository vaccineRepository,
                          final HospitalizationRepository hospitalizationRepository) {
        this.coronaRepository = coronaRepository;
        this.vaccineRepository = vaccineRepository;
        this.hospitalizationRepository = hospitalizationRepository;
    }

    public Tweet createStatisticsTweet(Corona coronaToday) {
        String rEmoji = "→";

        Optional<CoronaDAO> optCoronaYesterday = coronaRepository.findById(LocalDate.now().minusDays(1));
        if (optCoronaYesterday.isPresent()) {
            CoronaDAO coronaYesterday = optCoronaYesterday.get();
            rEmoji = coronaYesterday.getRValue() > coronaToday.getRValue().getRValue7Days().getValue() ? "↘" : "↗";
        }

        return new Tweet("\uD83C\uDDE9\uD83C\uDDEA #Corona Statistiken am " + LocalDate.now().format(dateTimeFormatter) + " \uD83C\uDDE9\uD83C\uDDEA\n" +
                "\n🦠 Aktiv Infiziert: " + numberFormatter.format(coronaToday.getCases() - coronaToday.getRecovered() - coronaToday.getDeaths()) +
                "\n☠ Todesfälle: " + numberFormatter.format(coronaToday.getDeaths()) + " (+" + numberFormatter.format(coronaToday.getDifference().getDeaths()) + ")" +
                "\n🏥 Genesen: +" + numberFormatter.format(coronaToday.getDifference().getRecovered()) +
                "\n\n" +
                "⚠ Neuinfektionen: +" + numberFormatter.format((coronaToday.getDifference().getCases())) +
                "\n" + rEmoji + " 7 Tage R-Wert: " + (decimalFormatter.format(coronaToday.getRValue().getRValue7Days().getValue()).replace(".", ",")) +
                "\n\n\uD83E\uDE84 Inzidenz: " + decimalFormatter.format(coronaToday.getWeekIncidence()).replace(".", ","));
    }

    public Tweet createConclusionTweet() {
        String startOfWeek = LocalDate.now().minusDays(6).format(dateTimeFormatter);
        String endOfWeek = LocalDate.now().format(dateTimeFormatter);

        Integer weekInfections = coronaRepository.concludeInfectionsOfWeek();
        Integer weekDeaths = coronaRepository.concludeDeathsOfWeek();
        Integer weekRecovered = coronaRepository.concludeRecoveredOfWeek();

        Integer weekBasicVaccinations = vaccineRepository.concludeBasicVaccineOfWeek();
        Integer weekSecondVaccinations = vaccineRepository.concludeSecondVaccineOfWeek();

        return new Tweet("\uD83C\uDDE9\uD83C\uDDEA Zusammenfassung der Woche vom " + startOfWeek + " - " + endOfWeek + " \uD83C\uDDE9\uD83C\uDDEA\n" +
                "\n🦠 Neuinfektionen: +" + numberFormatter.format(weekInfections) +
                "\n☠ Todesfälle: +" + numberFormatter.format(weekDeaths) +
                "\n🏥 Genesen: +" + numberFormatter.format(weekRecovered) +
                "\n💉 Grundimmunisierte: +" + numberFormatter.format(weekBasicVaccinations) +
                "\n💉💉 vollst. Immunisiert: +" + numberFormatter.format(weekSecondVaccinations) +
                "\n\n\uD83D\uDCC8 Die #Corona Entwicklungen können in den folgenden Grafiken eingesehen werden:");
    }

    public Tweet createHospitalizationTweet(HospitalizationData hospitalizationData) {
        Optional<HospitalizationDAO> optionalHospitalization = hospitalizationRepository.findById(LocalDate.now().minusDays(1));
        int covidToIntensivBettenDifference = 0;
        if(optionalHospitalization.isPresent()) {
            covidToIntensivBettenDifference = hospitalizationData.getFaelleCovidAktuell() - optionalHospitalization.get().getCurrentCovidCases();
        }

        String text = "\uD83C\uDDE9\uD83C\uDDEA Auslastung der Intensivstationen am {datum} \uD83C\uDDE9\uD83C\uDDEA\n" +
                "\n" +
                "\uD83D\uDECC Belegt: {intensivBettenBelegt} / {intensivBettenGesamt} ({bettenBelegtToBettenGesamtPercent}% | {intensivBettenFrei} frei)\n" +
                "\n" +
                "\uD83E\uDDA0 Davon Corona: {faelleCovidAktuell} ({covidToIntensivBettenPercent}% | {covidToIntensivBettenDifference})\n" +
                "   \uD83D\uDD38 Davon Beatmet: {faelleCovidAktuellBeatmet} ({faelleCovidAktuellBeatmetToCovidAktuellPercent}%)\n" +
                "   \uD83D\uDD39 Betten frei pro Standort: {intensivBettenFreiProStandort}\n" +
                "\n" +
                "\uD83C\uDD93 Corona-Intensivbetten frei: {covidKapazitaetFrei} / {covidKapazitaetGesamt}";

        return new Tweet(text.replace("{datum}", LocalDate.now().format(dateTimeFormatter))
                .replace("{intensivBettenBelegt}", numberFormatter.format(hospitalizationData.getIntensivBettenBelegt()))
                .replace("{intensivBettenGesamt}", numberFormatter.format(hospitalizationData.getIntensivBettenGesamt()))
                .replace("{bettenBelegtToBettenGesamtPercent}", decimalFormatter.format(hospitalizationData.getBettenBelegtToBettenGesamtPercent()).replace(".", ","))
                .replace("{intensivBettenFrei}", numberFormatter.format(hospitalizationData.getIntensivBettenFrei()))
                .replace("{faelleCovidAktuell}", numberFormatter.format(hospitalizationData.getFaelleCovidAktuell()))
                .replace("{covidToIntensivBettenPercent}", decimalFormatter.format(hospitalizationData.getCovidToIntensivBettenPercent()).replace(".", ","))
                .replace("{covidToIntensivBettenDifference}", covidToIntensivBettenDifference > 0 ? "+"+ numberFormatter.format(covidToIntensivBettenDifference) : numberFormatter.format(covidToIntensivBettenDifference ))
                .replace("{faelleCovidAktuellBeatmet}", numberFormatter.format(hospitalizationData.getFaelleCovidAktuellBeatmet()))
                .replace("{faelleCovidAktuellBeatmetToCovidAktuellPercent}", decimalFormatter.format(hospitalizationData.getFaelleCovidAktuellBeatmetToCovidAktuellPercent()).replace(".", ","))
                .replace("{intensivBettenFreiProStandort}", decimalFormatter.format(hospitalizationData.getIntensivBettenFreiProStandort()).replace(".", ","))
                .replace("{covidKapazitaetFrei}", numberFormatter.format(hospitalizationData.getCovidKapazitaetFrei()))
                .replace("{covidKapazitaetGesamt}", numberFormatter.format(hospitalizationData.getCovidKapazitaetInsgesamt()))
        );

    }

    public Tweet createVaccineTweet(VaccineResponseWrapper vaccine) {
        Optional<VaccineDAO> vaccineDAO = vaccineRepository.findLastEntry();
        Double firstVaccineDiff = (vaccine.getData().getQuote() * 100) - vaccineDAO.get().getAlltimeFirstVaccinatedPercent();
        Double secondVaccineDiff = (vaccine.getData().getSecondVaccination().getQuote() * 100) - vaccineDAO.get().getAlltimeSecondVaccinatedPercent();
        NumberFormat numberFormatter = NumberFormat.getInstance(Locale.GERMAN);

        vaccine.getData().setVaccinePercentDiff(firstVaccineDiff);
        vaccine.getData().getSecondVaccination().setVaccinePercentDiff(secondVaccineDiff);

        String tweetText = "\uD83C\uDDE9\uD83C\uDDEA #Corona Impfstatus am {datum} \uD83C\uDDE9\uD83C\uDDEA" +
                "\n\n✅ Momentan geimpft: {firstVaccinePercent}% (+{firstVaccinePercentDiff}%)" +
                "\n\uD83D\uDEE1 Davon vollständig: {secondVaccinePercent}% (+{secondVaccinePercentDiff}%)" +
                "\n\n\uD83D\uDC89 Neue #Impfungen: +{newVaccinations}" +
                "\n    \uD83D\uDD38 Erstgeimpft: {new.firstVaccinations}" +
                "\n    \uD83D\uDD39 vollst. geimpft: {new.secondVaccinations}" +
                "\n    \uD83D\uDD38 Booster: {refreshedVaccinations}";

        return new Tweet(tweetText.replace("{datum}", LocalDate.now().format(dateTimeFormatter))
                .replace("{firstVaccinePercent}", decimalFormatter.format(vaccine.getData().getQuote() * 100))
                .replace("{firstVaccinePercentDiff}", decimalFormatter.format(firstVaccineDiff))
                .replace("{secondVaccinePercent}", decimalFormatter.format(vaccine.getData().getSecondVaccination().getQuote() * 100))
                .replace("{secondVaccinePercentDiff}", decimalFormatter.format(secondVaccineDiff))
                .replace("{newVaccinations}", numberFormatter.format(vaccine.getData().getDelta() + vaccine.getData().getSecondVaccination().getDelta() + vaccine.getData().getBooster().getDelta()))
                .replace("{new.firstVaccinations}", numberFormatter.format(vaccine.getData().getDelta()))
                .replace("{new.secondVaccinations}", numberFormatter.format(vaccine.getData().getSecondVaccination().getDelta()))
                .replace("{refreshedVaccinations}", numberFormatter.format(vaccine.getData().getBooster().getDelta()))
                );
    }
}
