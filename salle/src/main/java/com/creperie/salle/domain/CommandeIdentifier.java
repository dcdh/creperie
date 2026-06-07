package com.creperie.salle.domain;

import com.damdamdeo.pulse.extension.core.AggregateId;
import com.damdamdeo.pulse.extension.core.SequenceNumber;

import java.util.Objects;

public record CommandeIdentifier(NumeroDeTable numeroDeTable, SequenceNumber sequence) implements AggregateId {

    public CommandeIdentifier {
        Objects.requireNonNull(numeroDeTable);
        Objects.requireNonNull(sequence);
    }

    public static CommandeIdentifier from(final String value) {
        String[] split = value.split(SEPARATOR);
        return new CommandeIdentifier(new NumeroDeTable(Integer.valueOf(split[0])), new SequenceNumber(split[1]));
    }

    @Override
    public String id() {
        return numeroDeTable.numero() + SEPARATOR + sequence.number();
    }
}
