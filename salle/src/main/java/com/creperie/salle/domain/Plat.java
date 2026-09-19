package com.creperie.salle.domain;

import java.util.Objects;

public record Plat(String nom) {

    public Plat {
        Objects.requireNonNull(nom);
    }

    public static Plat from(String nom) {
        return new Plat(nom);
    }
}
