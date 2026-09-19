package com.creperie.cuisine.domain.command;

import com.creperie.cuisine.domain.Plat;
import com.creperie.cuisine.domain.PreparationIdentifier;
import com.damdamdeo.pulse.extension.core.command.CreationalCommand;

import java.util.List;
import java.util.Objects;

public record ProduireCommande(CommandeIdentifier commandeIdentifier,
                               List<Plat> plats) implements CreationalCommand<PreparationIdentifier> {

    public ProduireCommande {
        Objects.requireNonNull(commandeIdentifier);
        Objects.requireNonNull(plats);
    }
}
