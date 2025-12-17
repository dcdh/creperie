package com.creperie.cuisine.domain.command;

import com.creperie.cuisine.domain.PreparationIdentifier;
import com.damdamdeo.pulse.extension.core.command.Command;

public record MarkProductionTerminee(PreparationIdentifier id) implements Command<PreparationIdentifier> {
}
