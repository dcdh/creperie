package com.creperie.cuisine.domain.command;

import com.creperie.cuisine.domain.Plat;
import com.creperie.cuisine.domain.PreparationIdentifier;
import com.damdamdeo.pulse.extension.core.command.Command;

import java.util.List;
import java.util.Objects;

public record ProduireCommande(PreparationIdentifier id, List<Plat> plats) implements Command<PreparationIdentifier> {

    public ProduireCommande {
        Objects.requireNonNull(id);
        Objects.requireNonNull(plats);
    }
}
