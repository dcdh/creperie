package com.creperie.salle.domain;

import java.util.Objects;

public record NumeroDeTable(Integer numero) {

    public NumeroDeTable {
        Objects.requireNonNull(numero);
    }
}
