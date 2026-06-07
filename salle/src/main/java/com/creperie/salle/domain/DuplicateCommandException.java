package com.creperie.salle.domain;

import com.damdamdeo.pulse.extension.core.DuplicateAggregateException;

import java.util.Objects;

public class DuplicateCommandException extends DuplicateAggregateException {

    private final CommandeIdentifier commandeIdentifier;

    public DuplicateCommandException(final CommandeIdentifier commandeIdentifier) {
        this.commandeIdentifier = Objects.requireNonNull(commandeIdentifier);
    }
}
