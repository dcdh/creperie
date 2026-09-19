package com.creperie.cuisine.infrastructure.serializer;

import com.creperie.cuisine.domain.PreparationIdentifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public class RegisterSerializerCustomizer implements ObjectMapperCustomizer {

    public void customize(final ObjectMapper objectMapper) {
        final SimpleModule module = new SimpleModule();
        module.addSerializer(PreparationIdentifier.class, new PreparationIdentifierSerializer());
        module.addDeserializer(PreparationIdentifier.class, new PreparationIdentifierDeserializer());
        objectMapper.registerModule(module);
    }
}
