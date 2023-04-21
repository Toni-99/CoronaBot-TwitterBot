package dev.twagner.http;

import dev.twagner.model.TweetType;
import dev.twagner.model.persistence.CoronaDAO;
import dev.twagner.model.persistence.VaccineDAO;
import dev.twagner.repository.CoronaRepository;
import dev.twagner.repository.VaccineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/*
    Nothing to add here. String formatting at its best *hust*
 */

@Component
public class ImageLoader {

    private final String germanyMapUrlDistricts;
    private final String germanyMapUrlStates;
    private final String hospitalizationMap;
    private final CoronaRepository coronaRepository;
    private final VaccineRepository vaccineRepository;
    private final Twitter twitter;
    private final Logger LOG = LoggerFactory.getLogger(ImageLoader.class);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uu");
    private final DateTimeFormatter headlineDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
    public ImageLoader(@Value("${image.url.germany.districts}") final String germanyMapUrlDistricts,
                       @Value("${image.url.germany.states}") final String germanyMapUrlStates,
                       @Value("${image.url.hospitalization}") final String hospitalizationMap,
                       final CoronaRepository coronaRepository,
                       final VaccineRepository vaccineRepository,
                       final Twitter twitter) {
        this.germanyMapUrlDistricts = germanyMapUrlDistricts;
        this.germanyMapUrlStates = germanyMapUrlStates;
        this.hospitalizationMap = hospitalizationMap;
        this.coronaRepository = coronaRepository;
        this.vaccineRepository = vaccineRepository;
        this.twitter = twitter;
    }

    public long[] getImagesForTweet(TweetType tweetType) {
        List<String> images = List.of();
        switch (tweetType) {
            case STATISTICS:
                images = List.of(germanyMapUrlDistricts, germanyMapUrlStates, getUrlForWeekIncidenceGraph());
                break;
            case HOSPITALIZATION:
                images = List.of(hospitalizationMap);
                break;
            case CONCLUSION:
                images = List.of(getUrlForGraphInfections(), getUrlForGraphDeaths(), getUrlForGraphRecovered(), getUrlForGraphVaccinated());
                break;
            case VACCINE:
                images = List.of(getPieChartForVaccineMessage(), getOverviewChartForVaccineMessage());
                break;
        }
        return createMediaIdsForAttachments(images);
    }

    private long[] createMediaIdsForAttachments(List<String> images) {
        long[] mediaIds = new long[images.size()];
        for (int i = 0; i < images.size(); i++) {
            if(images.get(i).isEmpty()) {
                continue;
            }

            try {
                var uploadedMedia = twitter.uploadMedia("CoronaBot generic Chart", new URL(images.get(i)).openStream());
                mediaIds[i] = uploadedMedia.getMediaId();
            } catch (TwitterException | IOException e) {
                throw new RuntimeException(e);
            }

        }
        return mediaIds;
    }

    public String getUrlForWeekIncidenceGraph() {
        List<CoronaDAO> incidences = coronaRepository.getLast11Incidences();
        if (incidences.isEmpty()) {
            LOG.warn("(Inzidenzverlauf) Fehler beim Laden der letzten 14 Corona Daten aus der Datenbank");
            return "";
        }

        String linkToGraph = "https://chart.googleapis.com/chart" +
                "?cht=bvs" +
                "&chs=630x400" +
                "&chco=1D93D1" +
                "&chd=t:%minus10%,%minus9%,%minus8%,%minus7%,%minus6%,%minus5%,%minus4%,%minus3%,%minus2%,%minus1%,%minus0%" +
                "&chm=N,333333,0,-1,13" +
                "&chds=0,100" +
                "&chxt=x,y" +
                "&chxl=0:|%date.minus10%|%date.minus9%|%date.minus8%|%date.minus7%|%date.minus6%|%date.minus5%|%date.minus4%|%date.minus3%|%date.minus2%|%date.minus1%|%date.minus0%|1:|0|10|20|30|40|50|60|70|80|90|100" +
                "&chtt=10-Tage-Inzidenzverlauf+-+Stand:+%datum%|©+CoronaBot+Deutschland" +
                "&chts=333333,16,c" +
                "&chbh=35,13,20" +
                "&chg=0,10" +
                "&chma=50,0,0,0";


        linkToGraph = linkToGraph.replace("%datum%", LocalDate.now().format(headlineDateTimeFormatter));

        // Replace recovered
        CoronaDAO actual;
        for (int i = 0; i < incidences.size(); i++) {
            actual = incidences.get(i);
            linkToGraph = linkToGraph.replace("%minus" + i + "%", actual.getWeekIncidence().toString());
            linkToGraph = linkToGraph.replace("%date.minus" + i + "%", actual.getDate().format(dateTimeFormatter));
        }

        return linkToGraph;
    }

    public String getUrlForGraphInfections() {
        List<CoronaDAO> coronaList = coronaRepository.getLast14Coronas();
        if (coronaList.isEmpty()) {
            LOG.warn("(Infektionen) Fehler beim Laden der letzten 14 Corona Daten aus der Datenbank");
            return "";
        }

        String linkToGraph = "https://chart.googleapis.com/chart" +
                "?cht=ls&chtt=Wöchentliche%20Zusammenfassung%20(%from%%20-%20%until%)|©%20CoronaBot%20Deutschland" + // TITEL
                "&chs=600x400" +
                "&chco=EB4034,333333" +
                "&chxt=x,y" +
                "&chxl=0:|Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag|1:|0|20.000|40.000|60.000|80.000|100.000|120.000|140.000|160.000|180.000|200.000" + // Beschriftung X & Y
                "&chdl=Neuinfektionen|Vorwoche" +

                "&chd=t:%infections.6%,%infections.5%,%infections.4%,%infections.3%,%infections.2%,%infections.1%,%infections.0%|" +  //Reihenfolge: Neuinfektionen, Tode, Genesene, Impfungen // Impfungen
                "%infections.13%,%infections.12%,%infections.11%,%infections.10%,%infections.9%,%infections.8%,%infections.7%" +   // Neuinfektionen
                "&chof=png" +                                       // Ausgabeformat
                "&chds=0,200000" +                                  // Bereich in dem sich die Werte bewegen können
                "&chts=333333,16,c" +                               // Titelfarbe, Font Size und auf Center gesetzt
                "&chg=16.66,10.0";                                  // Hilfslinien zeichnen


        linkToGraph = linkToGraph.replace("%until%", LocalDate.now().format(headlineDateTimeFormatter));
        linkToGraph = linkToGraph.replace("%from%", LocalDate.now().minusDays(6).format(headlineDateTimeFormatter));

        // Replace infections
        for (int i = 0; i < coronaList.size(); i++) {
            linkToGraph = linkToGraph.replace("%infections." + i + "%", coronaList.get(i).getCases().toString());
        }

        return linkToGraph;
    }

    public String getUrlForGraphDeaths() {
        List<CoronaDAO> coronaList = coronaRepository.getLast14Coronas();
        if (coronaList.isEmpty()) {
            LOG.warn("(Todesfälle) Fehler beim Laden der letzten 14 Corona Daten aus der Datenbank");
            return "";
        }
        String linkToGraph = "https://chart.googleapis.com/chart" +
                "?cht=ls&chtt=Wöchentliche%20Zusammenfassung%20(%from%%20-%20%until%)|©%20CoronaBot%20Deutschland" + // TITEL
                "&chs=600x400" +
                "&chco=3D6ACC,333333" +
                "&chxt=x,y" +
                "&chxl=0:|Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag|1:|0|50|100|150|200|250|300|350|400|450|500" + // Beschriftung X & Y
                "&chdl=Todesfälle|Vorwoche" +
                "&chd=t:%deaths.6%,%deaths.5%,%deaths.4%,%deaths.3%,%deaths.2%,%deaths.1%,%deaths.0%|" +  //Reihenfolge: Neuinfektionen, Tode, Genesene, Impfungen // Impfungen
                "%deaths.13%,%deaths.12%,%deaths.11%,%deaths.10%,%deaths.9%,%deaths.8%,%deaths.7%" +  //Reihenfolge: Neuinfektionen, Tode, Genesene, Impfungen // Impfungen
                "&chof=png" +                                   // Ausgabeformat
                "&chds=0,500" +                                 // Bereich in dem sich die Werte bewegen können
                "&chts=333333,16,c" +                           // Titelfarbe, Font Size und auf Center gesetzt
                "&chg=16.66,10.0000";                           // Hilfslinien zeichnen

        linkToGraph = linkToGraph.replace("%until%", LocalDate.now().format(headlineDateTimeFormatter));
        linkToGraph = linkToGraph.replace("%from%", LocalDate.now().minusDays(6).format(headlineDateTimeFormatter));

        // Replace infections
        for (int i = 0; i < coronaList.size(); i++) {
            linkToGraph = linkToGraph.replace("%deaths." + i + "%", coronaList.get(i).getDeaths().toString());
        }

        return linkToGraph;
    }

    public String getUrlForGraphRecovered() {
        List<CoronaDAO> coronaList = coronaRepository.getLast14Coronas();

        if (coronaList.isEmpty()) {
            LOG.warn("(Genesene) Fehler beim Laden der letzten 14 Corona Daten aus der Datenbank");
            return "";
        }
        // TODO: Umbau der Queries auf UriComponentsBuilder
        //var linktoGraph= UriComponentsBuilder.fromUriString("https://chart.googleapis.com/chart")
        //        .buildAndExpand(Map.of("erster param", " estarster wertz"));

        String linkToGraph = "https://chart.googleapis.com/chart" +
                "?cht=ls" +
                "&chtt=Wöchentliche%20Zusammenfassung%20(%from%%20-%20%until%)|©%20CoronaBot%20Deutschland" + // TITEL
                "&chs=600x400" +
                "&chco=E6B729,333333" +
                "&chxt=x,y" +
                "&chxl=0:|Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag|1:|0|20.000|40.000|60.000|80.000|100.000|120.000|140.000|160.000|180.000|200.000" + // Beschriftung X & Y
                "&chdl=Genesen|Vorwoche" +
                "&chd=t:%recovered.6%,%recovered.5%,%recovered.4%,%recovered.3%,%recovered.2%,%recovered.1%,%recovered.0%|" +  //Reihenfolge: Neuinfektionen, Tode, Genesene, Impfungen // Impfungen
                "%recovered.13%,%recovered.12%,%recovered.11%,%recovered.10%,%recovered.9%,%recovered.8%,%recovered.7%" +  //Reihenfolge: Neuinfektionen, Tode, Genesene, Impfungen // Impfungen
                "&chof=png" +                                    // Ausgabeformat
                "&chds=0,200000" +                               // Bereich in dem sich die Werte bewegen können
                "&chts=333333,16,c" +                            // Titelfarbe, Font Size und auf Center gesetzt
                "&chg=16.66,10.000";                             // Hilfslinien zeichnen


        linkToGraph = linkToGraph.replace("%until%", LocalDate.now().format(headlineDateTimeFormatter));
        linkToGraph = linkToGraph.replace("%from%", LocalDate.now().minusDays(6).format(headlineDateTimeFormatter));

        // Replace recovered
        for (int i = 0; i < coronaList.size(); i++) {
            linkToGraph = linkToGraph.replace("%recovered." + i + "%", coronaList.get(i).getRecovered().toString());
        }

        return linkToGraph;
    }

    public String getUrlForGraphVaccinated() {
        List<VaccineDAO> vaccineList = vaccineRepository.getLastWeekVaccinationsForConclusion();

        if(vaccineList.isEmpty()) {
            LOG.warn("(Impfdaten Zusammenfassung) Fehler beim Laden der Impfdaten aus der Datenbank");
            return "";
        }
        String linkToGraph = "https://chart.googleapis.com/chart" +
                "?cht=ls" +
                "&chtt=Wöchentliche%20Zusammenfassung%20(%from%%20-%20%until%)|©%20CoronaBot%20Deutschland" + // TITEL
                "&chs=600x400" +
                "&chco=25DB50,333333" +
                "&chxt=x,y" +
                "&chxl=0:|Montag|Dienstag|Mittwoch|Donnerstag|Freitag|1:|0|500|1.000|1.500|2.000|2.500|3.000|3.500|4.000|4.500|5.000" + // Beschriftung X & Y
                "&chdl=Geimpft|Vorwoche" +
                "&chd=t:%vaccinated.4%,%vaccinated.3%,%vaccinated.2%,%vaccinated.1%,%vaccinated.0%|" +  //Reihenfolge: Neuinfektionen, Tode, Genesene, Impfungen // Impfungen
                "%vaccinated.9%,%vaccinated.8%,%vaccinated.7%,%vaccinated.6%,%vaccinated.5%" +  //Reihenfolge: Neuinfektionen, Tode, Genesene, Impfungen // Impfungen
                "&chof=png" +                                   // Ausgabeformat
                "&chds=0,5000" +                                // Bereich in dem sich die Werte bewegen können
                "&chts=333333,16,c" +                           // Titelfarbe, Font Size und auf Center gesetzt
                "&chg=25,10.0";                                 // Hilfslinien zeichnen


        linkToGraph = linkToGraph.replace("%until%", LocalDate.now().getDayOfMonth() + "." + LocalDate.now().getMonthValue() + "." + LocalDate.now().getYear());
        linkToGraph = linkToGraph.replace("%from%", LocalDate.now().minusDays(6).getDayOfMonth() + "." + LocalDate.now().minusDays(6).getMonthValue() + "." + LocalDate.now().minusDays(6).getYear());

        for (int i = 0; i < vaccineList.size(); i++) {
            linkToGraph = linkToGraph.replace("%vaccinated." + i + "%", vaccineList.get(i).getVaccinated().toString());
        }

        return linkToGraph;
    }

    public String getPieChartForVaccineMessage() {
        DecimalFormat decimalFormat = new DecimalFormat("###.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        Optional<VaccineDAO> vaccineDAOYesterday = vaccineRepository.findLastEntry();
        Optional<VaccineDAO> vaccineDAOToday = vaccineRepository.findById(LocalDate.now());
        int rest = 15;

        if(vaccineDAOToday.isEmpty() || vaccineDAOYesterday.isEmpty()) {
            LOG.warn("Fehler beim Laden der Impfdaten aus der Datenbank");
            return "";
        }
        double massImmunity = 85.000 - vaccineDAOToday.get().getAlltimeFirstVaccinatedPercent();

        String url = "https://chart.googleapis.com/chart" +
                "?chtt=Impfdaten+am+%datum%|©+CoronaBot+Deutschland" +
                "&chs=600x410" +
                "&chts=333333,16,c" +
                "&chl=%percent_second_vaccinations%%|%percent_second_vaccinations_new%%|%percent_first_vaccinations_string%%|%percent_new_vaccinations%%|%massimmunity%%" +
                "&chdl=Vollstaendig+geimpft|Vollstaendig+geimpft+(Neu)|Erstgeimpft|Erstgeimpft+(Neu)|Differenz+bis+85%" +
                "&chd=t:%percent_second_vaccinations%,%percent_second_vaccinations_new%,%percent_first_vaccinations%,%percent_new_vaccinations%,%massimmunity%,%rest%" +
                "&cht=p" +
                "&chp=-1.57" +
                "&chco=5572AB|666666|39BF5E|666666|A3A07A|8A6A6C";

        url = url.replace("%datum%", LocalDate.now().format(headlineDateTimeFormatter))
                .replace("%percent_second_vaccinations_new%", decimalFormat.format(vaccineDAOYesterday.get().getAlltimeSecondVaccinatedPercent() - vaccineDAOToday.get().getAlltimeSecondVaccinatedPercent()))
                .replace("%massimmunity%", decimalFormat.format(massImmunity))
                .replace("%percent_first_vaccinations%", decimalFormat.format(vaccineDAOToday.get().getAlltimeFirstVaccinatedPercent() - vaccineDAOToday.get().getAlltimeSecondVaccinatedPercent()))
                .replace("%percent_new_vaccinations%", decimalFormat.format(vaccineDAOToday.get().getAlltimeFirstVaccinatedPercent() - vaccineDAOYesterday.get().getAlltimeFirstVaccinatedPercent()))
                .replace("%rest%", decimalFormat.format(rest))
                .replace("%percent_second_vaccinations%", vaccineDAOToday.get().getAlltimeSecondVaccinatedPercent().toString())
                .replace("%percent_first_vaccinations_string%", decimalFormat.format(vaccineDAOToday.get().getAlltimeFirstVaccinatedPercent()));
        return url;
    }

    public String getOverviewChartForVaccineMessage() {
        Optional<VaccineDAO> vaccineDAOToday = vaccineRepository.findById(LocalDate.now());

        String url = "https://chart.googleapis.com/chart" +
                "?chtt=Übersicht+der+Impfungen+am+%datum%|©+CoronaBot+Deutschland|" +
                "&cht=bvs" +
                "&chxt=x,y" +
                "&chxl=0:|Erstgeimpft|Vollständig+geimpft|Booster|1:|0|100|200|300|400|500|600|700|800|900|1.000" +
                "&chds=0,1000" +
                "&chs=500x350" +
                "&chd=t:%first%,%second%,%booster%" +
                "&chbh=105,35,50&chco=5572AB|39BF5E|A3A07A" +
                "&chg=0,10.0000";

        url = url.replace("%datum%", LocalDate.now().format(headlineDateTimeFormatter))
                .replace("%first%", String.valueOf(vaccineDAOToday.get().getVaccinated()))
                .replace("%second%", String.valueOf(vaccineDAOToday.get().getSecondVaccination()))
                .replace("%booster%", String.valueOf(vaccineDAOToday.get().getBooster()));

        return url;
    }
}
