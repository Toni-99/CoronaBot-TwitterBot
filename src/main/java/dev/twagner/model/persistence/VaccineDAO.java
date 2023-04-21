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
@Table(name = "vaccinations")
public class VaccineDAO {

    @Id
    @Column(name = "datum")
    private LocalDate date;

    @Column(name = "geimpft")
    private Integer alltimeVaccinated;

    @Column(name = "neugeimpft")
    private Integer vaccinated;

    @Column(name = "impfprozent")
    private Double alltimeFirstVaccinatedPercent;

    @Column(name = "impfprozentplusheute")
    private Double currentFirstVaccinatedPercent;

    @Column(name = "zweiteimpfung")
    private Integer alltimeSecondVaccination;

    @Column(name = "zweiteimpfungneu")
    private Integer secondVaccination;

    @Column(name = "zweitimpfungprozent")
    private Double alltimeSecondVaccinatedPercent;

    @Column(name = "zweitimpfungprozentplusheute")
    private Double currentSecondVaccinatedPercent;

    @Column(name = "booster")
    private Integer booster;
}
