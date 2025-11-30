package com.creperie.salle.domain.event;

import com.damdamdeo.pulse.extension.core.event.Event;

import java.util.Objects;

public record CommandeEnCoursDePrise(Integer nombreDeConvives) implements Event {

    public CommandeEnCoursDePrise {
        Objects.requireNonNull(nombreDeConvives);
    }
}
