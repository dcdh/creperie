package com.creperie.cuisine.infrastructure.api;

import com.creperie.cuisine.domain.Plat;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection(registerFullHierarchy = true)
@Schema(name = "Plat", required = true, requiredProperties = {"nom"})
public record PlatDTO(
        @Schema(type = SchemaType.STRING, implementation = String.class)
        String nom) {

    public static PlatDTO from(final Plat plat) {
        return new PlatDTO(plat.nom());
    }
}
