package com.creperie.cuisine.domain;

import java.util.Objects;

public record Plat(String nom) {

    public Plat {
        Objects.requireNonNull(nom);
    }
}
