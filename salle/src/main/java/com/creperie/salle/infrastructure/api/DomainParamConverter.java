package com.creperie.salle.infrastructure.api;

import com.creperie.salle.domain.CommandeIdentifier;
import com.damdamdeo.pulse.extension.core.obfuscator.Obfuscator;
import com.damdamdeo.pulse.extension.core.obfuscator.UnableToDeObfuscateException;
import com.damdamdeo.pulse.extension.core.obfuscator.UnableToObfuscateException;
import com.damdamdeo.pulse.extension.core.obfuscator.UnknownObfuscatedException;
import jakarta.inject.Inject;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class DomainParamConverter implements ParamConverterProvider {

    @Inject
    Obfuscator obfuscator;

    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
        if (rawType.equals(CommandeIdentifier.class)) {
            return (ParamConverter<T>) new ParamConverter<CommandeIdentifier>() {
                @Override
                public CommandeIdentifier fromString(final String value) {
                    if (value == null || value.isBlank()) {
                        return null;
                    }
                    try {
                        return CommandeIdentifier.from(obfuscator.deObfuscate(value));
                    } catch (final UnableToDeObfuscateException | UnknownObfuscatedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public String toString(final CommandeIdentifier value) {
                    try {
                        return obfuscator.obfuscate(value.id()).toString();
                    } catch (final UnableToObfuscateException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
        return null;
    }
}
