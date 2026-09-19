package com.creperie.salle.domain;

import java.util.Objects;

public record NumeroDeTable(Integer numero) {

    public NumeroDeTable {
        Objects.requireNonNull(numero);
    }

    public static NumeroDeTable from(final Integer value) {
        return new NumeroDeTable(value);
    }

    public static NumeroDeTable from(final String value) {
        return new NumeroDeTable(Integer.valueOf(value));
    }
}
