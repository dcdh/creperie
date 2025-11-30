package com.creperie.salle.domain.command;

import com.creperie.salle.domain.CommandeIdentifier;
import com.creperie.salle.domain.DatePriseDeCommande;
import com.creperie.salle.domain.NombreDeConvives;
import com.creperie.salle.domain.NumeroDeTable;
import com.damdamdeo.pulse.extension.core.command.Command;

import java.util.Objects;

public record CommencerLaPriseDeCommande(NombreDeConvives nombreDeConvives,
                                         NumeroDeTable numeroDeTable,
                                         DatePriseDeCommande datePriseDeCommande) implements Command<CommandeIdentifier> {

    public CommencerLaPriseDeCommande {
        Objects.requireNonNull(nombreDeConvives);
        Objects.requireNonNull(numeroDeTable);
    }

    @Override
    public CommandeIdentifier id() {
        return new CommandeIdentifier(numeroDeTable, datePriseDeCommande);
    }
}
