package com.creperie.cuisine.infrastructure.serializer;

import com.creperie.cuisine.domain.PreparationIdentifier;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Objects;

public class PreparationIdentifierSerializer extends StdSerializer<PreparationIdentifier> {

    public PreparationIdentifierSerializer() {
        super(PreparationIdentifier.class);
    }

    @Override
    public void serialize(final PreparationIdentifier value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        Objects.requireNonNull(value);
        Objects.requireNonNull(gen);
        Objects.requireNonNull(provider);
        gen.writeString(value.id());
    }
}
