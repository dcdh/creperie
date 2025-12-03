package com.creperie.salle.domain.event;

import com.creperie.salle.domain.Plat;
import com.damdamdeo.pulse.extension.core.event.Event;

import java.util.Objects;

public record PlatAjoute(String nom) implements Event {

    public PlatAjoute {
        Objects.requireNonNull(nom);
    }

    public Plat plat() {
        return new Plat(nom);
    }
}
