package com.creperie.gestion.domain;

import org.apache.commons.lang3.Validate;

import java.util.Objects;

public class FrequentationStatistique {

    private DateDeService dateDeService;
    private Integer nombreDeClients;

    public FrequentationStatistique(final DateDeService dateDeService, final Integer nombreDeClients) {
        this.dateDeService = Objects.requireNonNull(dateDeService);
        this.nombreDeClients = Objects.requireNonNull(nombreDeClients);
        Validate.isTrue(nombreDeClients >= 0);
    }

    public void ajouterConvives(final Integer convives) {
        this.nombreDeClients += convives;
    }

    public DateDeService dateDeService() {
        return dateDeService;
    }

    public Integer nombreDeClients() {
        return nombreDeClients;
    }
}
