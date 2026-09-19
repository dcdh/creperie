package com.creperie.salle.infrastructure.serializer;

import com.creperie.salle.domain.Plat;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Objects;

public class PlatDeserializer extends StdDeserializer<Plat> {

    public PlatDeserializer() {
        super(Plat.class);
    }

    @Override
    public Plat deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        Objects.requireNonNull(p);
        Objects.requireNonNull(ctxt);
        return Plat.from(p.getValueAsString());
    }
}
