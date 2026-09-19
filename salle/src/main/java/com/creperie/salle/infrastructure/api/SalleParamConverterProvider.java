package com.creperie.salle.infrastructure.api;

import com.creperie.salle.domain.CommandeIdentifier;
import com.creperie.salle.domain.NombreDeConvives;
import com.creperie.salle.domain.NumeroDeTable;
import com.creperie.salle.domain.Plat;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Provider
public class SalleParamConverterProvider implements ParamConverterProvider {

    private static final Map<Class<?>, Function<String, ?>> CONVERTERS = Map.of(
            CommandeIdentifier.class, CommandeIdentifier::from,
            Plat.class, Plat::from,
            NombreDeConvives.class, NombreDeConvives::from,
            NumeroDeTable.class, NumeroDeTable::from
    );

    private static final Map<Class<?>, Function<Object, String>> SERIALIZERS = Map.of(
            CommandeIdentifier.class, value -> ((CommandeIdentifier) value).id(),
            Plat.class, value -> ((Plat) value).nom(),
            NombreDeConvives.class, value -> ((NombreDeConvives) value).nombre().toString(),
            NumeroDeTable.class, value -> ((NumeroDeTable) value).numero().toString()
    );

    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
        final Function<String, ?> converter = CONVERTERS.get(rawType);
        final Function<Object, String> serializer = SERIALIZERS.get(rawType);
        if (converter == null) {
            return null;
        }
        return new ParamConverter<>() {

            @Override
            public T fromString(final String value) {
                Objects.requireNonNull(value);
                return rawType.cast(converter.apply(value));
            }

            @Override
            public String toString(final T value) {
                Objects.requireNonNull(value);
                return serializer.apply(value);
            }
        };
    }
}
