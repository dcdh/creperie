package com.creperie.salle.domain.command;

import com.creperie.salle.domain.CommandeIdentifier;
import com.creperie.salle.domain.Plat;
import com.damdamdeo.pulse.extension.core.command.Command;

import java.util.Objects;

public record AjouterPlat(CommandeIdentifier id, Plat plat) implements Command<CommandeIdentifier> {

    public AjouterPlat {
        Objects.requireNonNull(id);
        Objects.requireNonNull(plat);
    }

    @Override
    public CommandeIdentifier id() {
        return id;
    }
}
