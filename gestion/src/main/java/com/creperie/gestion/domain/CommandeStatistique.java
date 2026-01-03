package com.creperie.gestion.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CommandeStatistique {

    @JsonProperty("dateDeService")
    private DateDeService dateDeService;
    @JsonProperty("nombreDeCommandesEnCoursDePrise")
    private Integer nombreDeCommandesEnCoursDePrise;
    @JsonProperty("nombreDeCommandesFinalisees")
    private Integer nombreDeCommandesFinalisees;
    @JsonProperty("nombreDeCommandesEnProduction")
    private Integer nombreDeCommandesEnProduction;
    @JsonProperty("nombreDeCommandeProduite")
    private Integer nombreDeCommandeProduite;
    @JsonProperty("plats")
    private Map<String, Integer> plats;

    @JsonCreator
    public CommandeStatistique(final DateDeService dateDeService,
                               final Integer nombreDeCommandesEnCoursDePrise,
                               final Integer nombreDeCommandesFinalisees,
                               final Integer nombreDeCommandesEnProduction,
                               final Integer nombreDeCommandeProduite,
                               final Map<String, Integer> plats) {
        this.dateDeService = Objects.requireNonNull(dateDeService);
        this.nombreDeCommandesEnCoursDePrise = Objects.requireNonNull(nombreDeCommandesEnCoursDePrise);
        this.nombreDeCommandesFinalisees = Objects.requireNonNull(nombreDeCommandesFinalisees);
        this.nombreDeCommandesEnProduction = Objects.requireNonNull(nombreDeCommandesEnProduction);
        this.nombreDeCommandeProduite = Objects.requireNonNull(nombreDeCommandeProduite);
        this.plats = Objects.requireNonNull(plats);
        Validate.validState(this.nombreDeCommandesEnCoursDePrise >= 0, "nombreDeCommandesEnCoursDePrise must be >= 0");
        Validate.validState(this.nombreDeCommandesFinalisees >= 0, "nombreDeCommandesFinalisees must be >= 0");
        Validate.validState(this.nombreDeCommandesEnProduction >= 0, "nombreDeCommandesEnProduction must be >= 0");
        Validate.validState(this.nombreDeCommandeProduite >= 0, "nombreDeCommandeProduite must be >= 0");
    }

    public void ajouterNouvelleCommandeEnCoursDePrise() {
        this.nombreDeCommandesEnCoursDePrise++;
    }

    public void nouvelleCommandeFinalisee() {
        if (this.nombreDeCommandesEnCoursDePrise > 0) {
            this.nombreDeCommandesEnCoursDePrise--;
        }
        this.nombreDeCommandesFinalisees++;
    }

    public void nouvelleCommandeEnProduction() {
        if (this.nombreDeCommandesFinalisees > 0) {
            this.nombreDeCommandesFinalisees--;
        }
        this.nombreDeCommandesEnProduction++;
    }

    public void nouvelleCommandeProduite() {
        if (this.nombreDeCommandesEnProduction > 0) {
            this.nombreDeCommandesEnProduction--;
        }
        this.nombreDeCommandeProduite++;
    }

    public void ajouterPlat(final String plat) {
        Objects.requireNonNull(plat);
        final int currentNombreDePlats = plats.getOrDefault(plat, 0);
        plats.put(plat, currentNombreDePlats + 1);
    }

    public DateDeService dateDeService() {
        return dateDeService;
    }

    public Integer nombreDeCommandes() {
        return nombreDeCommandesEnCoursDePrise + nombreDeCommandesFinalisees + nombreDeCommandesEnProduction + nombreDeCommandeProduite;
    }

    public Integer nombreDeCommandesEnCoursDePrise() {
        return nombreDeCommandesEnCoursDePrise;
    }

    public Integer nombreDeCommandesFinalisees() {
        return nombreDeCommandesFinalisees;
    }

    public Integer nombreDeCommandesEnProduction() {
        return nombreDeCommandesEnProduction;
    }

    public Integer nombreDeCommandeProduite() {
        return nombreDeCommandeProduite;
    }

    public Map<String, Integer> plats() {
        return plats;
    }
}
