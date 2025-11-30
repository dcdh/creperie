package com.creperie.salle.domain.event;

import com.creperie.salle.domain.Plat;
import com.damdamdeo.pulse.extension.core.event.Event;

import java.util.Objects;

public record PlatAjoute(String name) implements Event {

    public PlatAjoute {
        Objects.requireNonNull(name);
    }

    public Plat plat() {
        return new Plat(name);
    }
}
