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
@Table(name = "coronabot")
public class CoronaDAO {

    @Id
    @Column(name = "datum")
    private LocalDate date;

    @Column(name = "infektionen")
    private Integer alltimeCases;

    @Column(name = "verstorbene")
    private Integer alltimeDeaths;

    @Column(name = "genesen")
    private Integer alltimeRecovered;

    @Column(name = "neuinfektionen")
    private Integer cases;

    @Column(name = "neuverstorbene")
    private Integer deaths;

    @Column(name = "neugenesene")
    private Integer recovered;

    @Column(name = "weekincidence")
    private Double weekIncidence;

    @Column(name = "rfaktor")
    private Double rValue;
}
