package dev.twagner.model.persistence;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "beddata")
public class HospitalizationDAO {

    @Id
    @Column(name = "datum")
    private LocalDate date;

    @Column(name = "faellecovidaktuell")
    private Integer currentCovidCases;

    @Column(name = "faellecovidaktuellbeatmet")
    private Integer currentCovidCasesWithOxygen;

    @Column(name = "intensivbettengesamt")
    private Integer totalBeds;

    @Column(name = "intensivbettenbelegt")
    private Integer totalBedsUsed;

    @Column(name = "intensivbettenfrei")
    private Integer totalBedsFree;

    @Column(name = "covidkapazitaetfrei")
    private Integer covidCapacityFree;

    @Column(name = "covidkapazitaetinsgesamt")
    private Integer totalCovidCapacity;

    @Column(name = "covidtointensivbettenpercent")
    private Double covidToIntensivBettenPercent;

    @Column(name = "intensivbettenfreiprostandort")
    private Double freeBedsPerLocationAverage;

    @Column(name = "faellecovidaktuellbeatmettocovidaktuellpercent")
    private Double covidCasesCurrentOxygenToCovidActualPercent;

    @Column(name = "bettenbelegttobettengesamtpercent")
    private Double usedBedsToTotalBeds;

}
