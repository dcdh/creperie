package com.creperie.salle.domain;

import java.util.Objects;

public record NombreDeConvives(Integer nombre) {

    public NombreDeConvives {
        Objects.requireNonNull(nombre);
    }
}
