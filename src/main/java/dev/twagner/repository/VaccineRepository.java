package dev.twagner.repository;

import dev.twagner.model.persistence.VaccineDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*
    PagingAndSortingRepository would make sense too
 */

@Repository
public interface VaccineRepository extends JpaRepository<VaccineDAO, LocalDate> {

    @Query(value = "SELECT * FROM vaccinations ORDER BY datum DESC LIMIT 10", nativeQuery = true)
    List<VaccineDAO> getLastWeekVaccinationsForConclusion();

    @Query(value = "SELECT SUM(neugeimpft) FROM (SELECT neugeimpft FROM vaccinations ORDER BY datum DESC LIMIT 5) as neugeimpft", nativeQuery = true)
    Integer concludeBasicVaccineOfWeek();

    @Query(value = "SELECT SUM(zweiteimpfungneu) FROM (SELECT zweiteimpfungneu FROM vaccinations ORDER BY datum DESC LIMIT 5) as zweiteImpfungNeu", nativeQuery = true)
    Integer concludeSecondVaccineOfWeek();

    @Query(value = "SELECT * FROM vaccinations ORDER BY DATUM DESC LIMIT 1", nativeQuery = true)
    Optional<VaccineDAO> findLastEntry();
}
