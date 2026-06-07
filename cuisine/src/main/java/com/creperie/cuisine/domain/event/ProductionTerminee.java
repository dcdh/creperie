package com.creperie.cuisine.domain.event;

import com.creperie.cuisine.domain.PreparationIdentifier;
import com.damdamdeo.pulse.extension.core.event.Event;

public record ProductionTerminee() implements Event<PreparationIdentifier> {

}
