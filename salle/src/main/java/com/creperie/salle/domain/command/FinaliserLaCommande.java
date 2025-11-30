package com.creperie.salle.domain.command;

import com.creperie.salle.domain.CommandeIdentifier;
import com.damdamdeo.pulse.extension.core.command.Command;

import java.util.Objects;

public record FinaliserLaCommande(CommandeIdentifier id) implements Command<CommandeIdentifier> {

    public FinaliserLaCommande {
        Objects.requireNonNull(id);
    }
}
