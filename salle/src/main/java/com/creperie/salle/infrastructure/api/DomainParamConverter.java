package com.creperie.salle.infrastructure.api;

import com.creperie.salle.domain.CommandeIdentifier;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class DomainParamConverter implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
        if (rawType.equals(CommandeIdentifier.class)) {
            return (ParamConverter<T>) new ParamConverter<CommandeIdentifier>() {
                @Override
                public CommandeIdentifier fromString(final String value) {
                    if (value == null || value.isBlank()) {
                        return null;
                    }
                    return CommandeIdentifier.from(value);
                }

                @Override
                public String toString(final CommandeIdentifier value) {
                    return value.id();
                }
            };
        }
        return null;
    }
}
