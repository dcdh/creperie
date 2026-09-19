package com.creperie.salle.infrastructure.serializer;

import com.creperie.salle.domain.CommandeIdentifier;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Objects;

public class CommandeIdentifierDeserializer extends StdDeserializer<CommandeIdentifier> {

    public CommandeIdentifierDeserializer() {
        super(CommandeIdentifier.class);
    }

    @Override
    public CommandeIdentifier deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        Objects.requireNonNull(p);
        Objects.requireNonNull(ctxt);
        return CommandeIdentifier.from(p.getValueAsString());
    }
}
