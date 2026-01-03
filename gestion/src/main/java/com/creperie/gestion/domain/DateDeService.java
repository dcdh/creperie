package com.creperie.gestion.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record DateDeService(@JsonProperty("localDate") LocalDate localDate) {

    @JsonCreator
    public DateDeService(final LocalDate localDate) {
        this.localDate = Objects.requireNonNull(localDate);
    }

    public static DateDeService from(Instant creationDate) {
        return new DateDeService(creationDate.atZone(ZoneId.systemDefault()).toLocalDate());
    }
}
