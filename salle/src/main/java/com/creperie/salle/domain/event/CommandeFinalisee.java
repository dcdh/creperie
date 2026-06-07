package com.creperie.salle.domain.event;

import com.creperie.salle.domain.CommandeIdentifier;
import com.damdamdeo.pulse.extension.core.event.Event;

public record CommandeFinalisee() implements Event<CommandeIdentifier> {

}
