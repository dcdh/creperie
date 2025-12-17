package com.creperie.salle.domain.event;

import com.creperie.salle.domain.NombreDeConvives;
import com.damdamdeo.pulse.extension.core.event.Event;

import java.util.Objects;

public record CommandeEnCoursDePrise(NombreDeConvives nombreDeConvives) implements Event {

    public CommandeEnCoursDePrise {
        Objects.requireNonNull(nombreDeConvives);
    }
}
