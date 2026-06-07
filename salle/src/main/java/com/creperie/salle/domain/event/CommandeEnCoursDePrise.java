package com.creperie.salle.domain.event;

import com.creperie.salle.domain.CommandeIdentifier;
import com.creperie.salle.domain.DatePriseDeCommande;
import com.creperie.salle.domain.NombreDeConvives;
import com.damdamdeo.pulse.extension.core.event.Event;

import java.util.Objects;

public record CommandeEnCoursDePrise(NombreDeConvives nombreDeConvives, DatePriseDeCommande datePriseDeCommande)
        implements Event<CommandeIdentifier> {

    public CommandeEnCoursDePrise {
        Objects.requireNonNull(nombreDeConvives);
        Objects.requireNonNull(datePriseDeCommande);
    }
}
