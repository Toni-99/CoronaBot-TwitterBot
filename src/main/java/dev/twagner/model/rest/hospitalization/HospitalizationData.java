package dev.twagner.model.rest.hospitalization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.twagner.model.persistence.HospitalizationDAO;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HospitalizationData {
    private final Integer faelleCovidAktuell;
    private final Integer faelleCovidAktuellBeatmet;
    private final Integer intensivBettenGesamt;
    private final Integer intensivBettenBelegt;
    private final Integer intensivBettenFrei;
    private final Integer covidKapazitaetFrei;
    private final Integer covidKapazitaetInsgesamt;
    private final Double covidToIntensivBettenPercent;
    private final Double intensivBettenFreiProStandort;
    private final Double faelleCovidAktuellBeatmetToCovidAktuellPercent;
    private final Double bettenBelegtToBettenGesamtPercent;

    // Prepare yourself for the best denglisch you will ever see in an API

    public HospitalizationData(@JsonProperty("faelleCovidAktuell") Integer faelleCovidAktuell,
                               @JsonProperty("faelleCovidAktuellBeatmet") Integer faelleCovidAktuellBeatmet,
                               @JsonProperty("intensivBettenGesamt") Integer intensivBettenGesamt,
                               @JsonProperty("intensivBettenBelegt") Integer intensivBettenBelegt,
                               @JsonProperty("intensivBettenFrei") Integer intensivBettenFrei,
                               @JsonProperty("covidKapazitaetFrei") Integer covidKapazitaetFrei,
                               @JsonProperty("covidToIntensivBettenPercent") Double covidToIntensivBettenPercent,
                               @JsonProperty("intensivBettenFreiProStandort") Double intensivBettenFreiProStandort,
                               @JsonProperty("faelleCovidAktuellBeatmetToCovidAktuellPercent") Double faelleCovidAktuellBeatmetToCovidAktuellPercent,
                               @JsonProperty("bettenBelegtToBettenGesamtPercent") Double bettenBelegtToBettenGesamtPercent) {
        this.faelleCovidAktuell = faelleCovidAktuell;
        this.faelleCovidAktuellBeatmet = faelleCovidAktuellBeatmet;
        this.intensivBettenGesamt = intensivBettenGesamt;
        this.intensivBettenBelegt = intensivBettenBelegt;
        this.intensivBettenFrei = intensivBettenFrei;
        this.covidKapazitaetFrei = covidKapazitaetFrei;
        this.covidKapazitaetInsgesamt = faelleCovidAktuell + covidKapazitaetFrei;
        this.covidToIntensivBettenPercent = covidToIntensivBettenPercent;
        this.intensivBettenFreiProStandort = intensivBettenFreiProStandort;
        this.faelleCovidAktuellBeatmetToCovidAktuellPercent = faelleCovidAktuellBeatmetToCovidAktuellPercent;
        this.bettenBelegtToBettenGesamtPercent = bettenBelegtToBettenGesamtPercent;
    }

    public HospitalizationDAO toHospitalizationDAO() {
        return new HospitalizationDAO(
                LocalDate.now(),
                faelleCovidAktuell,
                faelleCovidAktuellBeatmet,
                intensivBettenGesamt,
                intensivBettenBelegt,
                intensivBettenFrei,
                covidKapazitaetFrei,
                covidKapazitaetInsgesamt,
                covidToIntensivBettenPercent,
                intensivBettenFreiProStandort,
                faelleCovidAktuellBeatmetToCovidAktuellPercent,
                bettenBelegtToBettenGesamtPercent
        );
    }
}
