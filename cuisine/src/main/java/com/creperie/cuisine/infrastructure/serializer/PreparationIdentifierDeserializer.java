package com.creperie.cuisine.infrastructure.serializer;

import com.creperie.cuisine.domain.PreparationIdentifier;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Objects;

public class PreparationIdentifierDeserializer extends StdDeserializer<PreparationIdentifier> {

    public PreparationIdentifierDeserializer() {
        super(PreparationIdentifier.class);
    }

    @Override
    public PreparationIdentifier deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        Objects.requireNonNull(p);
        Objects.requireNonNull(ctxt);
        return PreparationIdentifier.from(p.getValueAsString());
    }
}
