package com.creperie.cuisine.domain;

import com.damdamdeo.pulse.extension.core.AggregateId;

import java.util.Objects;

public record PreparationIdentifier(String id) implements AggregateId {

    public PreparationIdentifier {
        Objects.requireNonNull(id);
    }

    public static PreparationIdentifier from(String value) {
        return new PreparationIdentifier(value);
    }
}
