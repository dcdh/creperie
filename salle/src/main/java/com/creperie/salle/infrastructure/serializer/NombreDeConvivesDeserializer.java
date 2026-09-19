package com.creperie.salle.infrastructure.serializer;

import com.creperie.salle.domain.NombreDeConvives;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Objects;

public class NombreDeConvivesDeserializer extends StdDeserializer<NombreDeConvives> {

    public NombreDeConvivesDeserializer() {
        super(NombreDeConvives.class);
    }

    @Override
    public NombreDeConvives deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        Objects.requireNonNull(p);
        Objects.requireNonNull(ctxt);
        return NombreDeConvives.from(p.getIntValue());
    }
}
