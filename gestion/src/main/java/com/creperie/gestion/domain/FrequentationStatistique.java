package com.creperie.gestion.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FrequentationStatistique {

    @JsonProperty("dateDeService")
    private DateDeService dateDeService;
    @JsonProperty("nombreDeClients")
    private Integer nombreDeClients;

    @JsonCreator
    public FrequentationStatistique(final DateDeService dateDeService,
                                    final Integer nombreDeClients) {
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
