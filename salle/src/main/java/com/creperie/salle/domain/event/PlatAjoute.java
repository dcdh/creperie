package com.creperie.salle.domain.event;

import com.creperie.salle.domain.CommandeIdentifier;
import com.creperie.salle.domain.Plat;
import com.damdamdeo.pulse.extension.core.event.Event;

import java.util.Objects;

public record PlatAjoute(Plat plat) implements Event<CommandeIdentifier> {

    public PlatAjoute {
        Objects.requireNonNull(plat);
    }
}
