package com.creperie.cuisine.domain.command;

import java.util.Objects;

public record CommandeIdentifier(String id) {

    public CommandeIdentifier {
        Objects.requireNonNull(id);
    }
}
