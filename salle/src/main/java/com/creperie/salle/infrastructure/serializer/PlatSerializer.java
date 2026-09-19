package com.creperie.salle.infrastructure.serializer;

import com.creperie.salle.domain.Plat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Objects;

public class PlatSerializer extends StdSerializer<Plat> {

    public PlatSerializer() {
        super(Plat.class);
    }

    @Override
    public void serialize(final Plat value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        Objects.requireNonNull(value);
        Objects.requireNonNull(gen);
        Objects.requireNonNull(provider);
        gen.writeString(value.nom());
    }
}
