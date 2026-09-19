package com.creperie.salle.infrastructure.serializer;

import com.creperie.salle.domain.CommandeIdentifier;
import com.creperie.salle.domain.NombreDeConvives;
import com.creperie.salle.domain.NumeroDeTable;
import com.creperie.salle.domain.Plat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public class RegisterSerializerCustomizer implements ObjectMapperCustomizer {

    public void customize(final ObjectMapper objectMapper) {
        final SimpleModule traceabilityMapperModule = new SimpleModule();
        traceabilityMapperModule.addSerializer(CommandeIdentifier.class, new CommandeIdentifierSerializer());
        traceabilityMapperModule.addDeserializer(CommandeIdentifier.class, new CommandeIdentifierDeserializer());
        traceabilityMapperModule.addSerializer(NumeroDeTable.class, new NumeroDeTableSerializer());
        traceabilityMapperModule.addDeserializer(NumeroDeTable.class, new NumeroDeTableDeserializer());
        traceabilityMapperModule.addSerializer(NombreDeConvives.class, new NombreDeConvivesSerializer());
        traceabilityMapperModule.addDeserializer(NombreDeConvives.class, new NombreDeConvivesDeserializer());
        traceabilityMapperModule.addSerializer(Plat.class, new PlatSerializer());
        traceabilityMapperModule.addDeserializer(Plat.class, new PlatDeserializer());
        objectMapper.registerModule(traceabilityMapperModule);
    }
}
