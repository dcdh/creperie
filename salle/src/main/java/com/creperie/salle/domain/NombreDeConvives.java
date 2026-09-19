package com.creperie.salle.domain;

import java.util.Objects;

public record NombreDeConvives(Integer nombre) {

    public NombreDeConvives {
        Objects.requireNonNull(nombre);
    }

    public static NombreDeConvives from(final Integer value) {
        return new NombreDeConvives(value);
    }

    public static NombreDeConvives from(final String value) {
        return new NombreDeConvives(Integer.valueOf(value));
    }
}
