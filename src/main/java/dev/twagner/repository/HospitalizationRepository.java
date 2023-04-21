package dev.twagner.repository;

import dev.twagner.model.persistence.HospitalizationDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HospitalizationRepository extends JpaRepository<HospitalizationDAO, LocalDate> {

}
