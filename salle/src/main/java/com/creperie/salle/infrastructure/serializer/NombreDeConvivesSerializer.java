package com.creperie.salle.infrastructure.serializer;

import com.creperie.salle.domain.NombreDeConvives;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Objects;

public class NombreDeConvivesSerializer extends StdSerializer<NombreDeConvives> {

    public NombreDeConvivesSerializer() {
        super(NombreDeConvives.class);
    }

    @Override
    public void serialize(final NombreDeConvives value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        Objects.requireNonNull(value);
        Objects.requireNonNull(gen);
        Objects.requireNonNull(provider);
        gen.writeNumber(value.nombre());
    }
}
