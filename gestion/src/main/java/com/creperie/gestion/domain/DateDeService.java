package com.creperie.gestion.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public record DateDeService(LocalDate localDate) {

    public DateDeService {
        Objects.requireNonNull(localDate);
    }

    public static DateDeService from(Instant creationDate) {
        return new DateDeService(LocalDate.from(creationDate));
    }
}
