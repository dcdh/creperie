package com.creperie.gestion.domain;

import java.util.Objects;

public record Plat(String nom) {

    public Plat {
        Objects.requireNonNull(nom);
    }
}
