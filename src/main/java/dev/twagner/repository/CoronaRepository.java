package dev.twagner.repository;

import dev.twagner.model.persistence.CoronaDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/*
    PagingAndSortingRepository would make sense too
 */

@Repository
public interface CoronaRepository extends JpaRepository<CoronaDAO, LocalDate> {

    @Query(value = "SELECT * FROM coronabot ORDER BY datum DESC LIMIT 11", nativeQuery = true)
    List<CoronaDAO> getLast11Incidences();

    @Query(value = "SELECT * FROM coronabot ORDER BY datum DESC LIMIT 14", nativeQuery = true)
    List<CoronaDAO> getLast14Coronas();

    @Query(value = "SELECT SUM(neuinfektionen) FROM (SELECT neuinfektionen FROM coronabot ORDER BY datum DESC LIMIT 7) as neuinfektionen", nativeQuery = true)
    Integer concludeInfectionsOfWeek();

    @Query(value = "SELECT SUM(neuverstorbene) FROM (SELECT neuverstorbene FROM coronabot ORDER BY datum DESC LIMIT 7) as neuverstorbene", nativeQuery = true)
    Integer concludeDeathsOfWeek();

    @Query(value = "SELECT SUM(neugenesene) FROM (SELECT neugenesene FROM coronabot ORDER BY datum DESC LIMIT 7) as neugenesene", nativeQuery = true)
    Integer concludeRecoveredOfWeek();

}
