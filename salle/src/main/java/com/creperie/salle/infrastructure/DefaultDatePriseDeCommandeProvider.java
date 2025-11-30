package com.creperie.salle.infrastructure;

import com.creperie.salle.domain.DatePriseDeCommande;
import com.creperie.salle.domain.DatePriseDeCommandeProvider;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;

@ApplicationScoped
public class DefaultDatePriseDeCommandeProvider implements DatePriseDeCommandeProvider {

    @Override
    public DatePriseDeCommande provide() {
        return new DatePriseDeCommande(Instant.now());
    }
}
