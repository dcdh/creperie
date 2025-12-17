package com.creperie.gestion.domain;

import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Objects;

public class CommandeStatistique {

    private DateDeService dateDeService;
    private Integer nombreDeCommandesEnCoursDePrise;
    private Integer nombreDeCommandesFinalisees;
    private Integer nombreDeCommandesEnProduction;
    private Integer nombreDeCommandeProduite;
    private Map<String, Integer> plats;

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
        this.nombreDeCommandesEnCoursDePrise--;
        this.nombreDeCommandesFinalisees++;
    }

    public void nouvelleCommandeEnProduction() {
        this.nombreDeCommandesFinalisees--;
        this.nombreDeCommandesEnProduction++;
    }

    public void nouvelleCommandeProduite() {
        this.nombreDeCommandesEnProduction--;
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
