package com.creperie.salle.infrastructure.serializer;

import com.creperie.salle.domain.NumeroDeTable;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Objects;

public class NumeroDeTableDeserializer extends StdDeserializer<NumeroDeTable> {

    public NumeroDeTableDeserializer() {
        super(NumeroDeTable.class);
    }

    @Override
    public NumeroDeTable deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        Objects.requireNonNull(p);
        Objects.requireNonNull(ctxt);
        return NumeroDeTable.from(p.getIntValue());
    }
}
