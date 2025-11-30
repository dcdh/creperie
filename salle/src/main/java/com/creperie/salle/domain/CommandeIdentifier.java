package com.creperie.salle.domain;

import com.damdamdeo.pulse.extension.core.AggregateId;

import java.util.Objects;

public record CommandeIdentifier(NumeroDeTable numeroDeTable,
                                 DatePriseDeCommande datePriseDeCommande) implements AggregateId {
    public CommandeIdentifier {
        Objects.requireNonNull(numeroDeTable);
        Objects.requireNonNull(datePriseDeCommande);
    }

    @Override
    public String id() {
        return "%d/%d".formatted(numeroDeTable.numero(), datePriseDeCommande.toEpochMilli());
    }
}
