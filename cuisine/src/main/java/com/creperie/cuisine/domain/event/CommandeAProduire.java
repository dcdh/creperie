package com.creperie.cuisine.domain.event;

import com.creperie.cuisine.domain.Plat;
import com.damdamdeo.pulse.extension.core.event.Event;

import java.util.List;
import java.util.Objects;

public record CommandeAProduire(List<Plat> plats) implements Event {

    public CommandeAProduire {
        Objects.requireNonNull(plats);
    }
}
