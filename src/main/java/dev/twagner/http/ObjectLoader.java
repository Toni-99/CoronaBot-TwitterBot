package dev.twagner.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.twagner.model.persistence.VaccineDAO;
import dev.twagner.model.rest.hospitalization.HospitalizationData;
import dev.twagner.model.rest.corona.Corona;
import dev.twagner.model.rest.vaccine.VaccineResponseWrapper;
import dev.twagner.repository.VaccineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class ObjectLoader {

    private final Logger LOG = LoggerFactory.getLogger(ObjectLoader.class);
    public final ObjectMapper objectMapper = new ObjectMapper();
    private final String germanyUrl;
    private final String hospitalizationUrl;
    private final String vaccineUrl;
    private final VaccineRepository vaccineRepository;

    public ObjectLoader(@Value(value = "${json.url.germany}") final String germanyUrl,
                        @Value(value = "${json.url.hospitalizationData}") final String hospitalizationUrl,
                        @Value(value = "${json.url.vaccine}") final String vaccineUrl,
                        final VaccineRepository vaccineRepository) {
        this.germanyUrl = germanyUrl;
        this.hospitalizationUrl = hospitalizationUrl;
        this.vaccineUrl = vaccineUrl;
        this.vaccineRepository = vaccineRepository;
    }

    public Optional<Corona> getCoronaStats() {
        Optional<Corona> corona = Optional.empty();
        try {
            corona = Optional.ofNullable(objectMapper.readValue(new URL(germanyUrl), Corona.class));
            LOG.info("Corona Daten geladen");
        } catch (IOException e) {
            LOG.error("Fehler beim Laden der Statistik-Daten", e);
        }
        return corona;
    }

    public Optional<HospitalizationData> getHospitalizationData() {
        Optional<HospitalizationData> hospitalizationData = Optional.empty();
        try {
            JsonNode json = objectMapper.readValue(new URL(hospitalizationUrl), JsonNode.class).get("overallSum");
            hospitalizationData = Optional.ofNullable(objectMapper.convertValue(json, HospitalizationData.class));
        } catch (IOException e) {
            LOG.error("Fehler beim Laden der Hospitalisierungsdaten", e);
        }
        return hospitalizationData;
    }

    public Optional<VaccineResponseWrapper> getVaccineStats() {
        Optional<VaccineResponseWrapper> vaccineData = Optional.empty();
        try {
            vaccineData = Optional.ofNullable(objectMapper.readValue(new URL(vaccineUrl), VaccineResponseWrapper.class));
        } catch (IOException e) {
            LOG.error("Fehler beim Laden der Impfdaten!", e);
        }
        return vaccineData;
    }
    public boolean checkForUpdates(VaccineResponseWrapper vaccineResponseWrapper) {
        if (vaccineRepository.findById(LocalDate.now()).isPresent()) {
            return false;
        }

        Optional<VaccineDAO> optVaccineDAO = vaccineRepository.findLastEntry();
        return optVaccineDAO
                .filter(vaccineDAO -> vaccineResponseWrapper.getData().getAdministeredVaccinations() > vaccineDAO.getAlltimeVaccinated())
                .isPresent();

    }
}
