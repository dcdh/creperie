package com.creperie.salle.domain.command;

import com.creperie.salle.domain.CommandeIdentifier;
import com.creperie.salle.domain.DatePriseDeCommande;
import com.creperie.salle.domain.NombreDeConvives;
import com.creperie.salle.domain.NumeroDeTable;
import com.damdamdeo.pulse.extension.core.command.CreationalCommand;

import java.util.Objects;

public record CommencerLaPriseDeCommande(NombreDeConvives nombreDeConvives,
                                         NumeroDeTable numeroDeTable,
                                         DatePriseDeCommande datePriseDeCommande) implements CreationalCommand<CommandeIdentifier> {

    public CommencerLaPriseDeCommande {
        Objects.requireNonNull(nombreDeConvives);
        Objects.requireNonNull(numeroDeTable);
    }
}
