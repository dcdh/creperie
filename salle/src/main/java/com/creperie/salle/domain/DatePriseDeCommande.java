package com.creperie.salle.domain;

import java.time.Instant;
import java.util.Objects;

public record DatePriseDeCommande(Instant date) {

    public DatePriseDeCommande {
        Objects.requireNonNull(date);
    }

    public Long toEpochMilli() {
        return date.toEpochMilli();
    }
}
