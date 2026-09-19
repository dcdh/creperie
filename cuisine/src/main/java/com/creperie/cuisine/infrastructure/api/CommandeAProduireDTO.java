package com.creperie.cuisine.infrastructure.api;

import com.creperie.cuisine.domain.PreparationIdentifier;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@RegisterForReflection(registerFullHierarchy = true)
@Schema(name = "CommandeAProduire", required = true, requiredProperties = {"id", "plats"})
public record CommandeAProduireDTO(
        @Schema(type = SchemaType.STRING, implementation = String.class)
        PreparationIdentifier id,
        List<PlatDTO> plats) {
}
