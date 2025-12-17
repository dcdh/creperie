package com.creperie.gestion.infrastructure.api;

import com.creperie.gestion.domain.AuditEvent;
import com.damdamdeo.pulse.extension.common.runtime.encryption.OpenPGPDecryptionService;
import com.damdamdeo.pulse.extension.core.encryption.DecryptedPayload;
import com.damdamdeo.pulse.extension.core.encryption.DecryptionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AuditEventDTOMapper {

    @Inject
    OpenPGPDecryptionService openPGPDecryptionService;

    @Inject
    ObjectMapper objectMapper;

    public AuditEventDTO mapFrom(final AuditEvent auditEvent) {
        try {
            final DecryptedPayload decrypted = openPGPDecryptionService.decrypt(auditEvent.encryptedPayload(), auditEvent.ownedBy());
            final JsonNode payload = objectMapper.readTree(decrypted.payload());
            final String message;
            switch (auditEvent.fromApplication().functionalDomain()) {
                case "Salle":
                    switch (auditEvent.eventType().type()) {
                        case "CommandeEnCoursDePrise":
                            message = "Commande en cours de prise pour %d convives".formatted(
                                    payload.get("nombreDeConvives").get("nombre").asInt());
                            break;
                        case "PlatAjoute":
                            message = "Plat %s ajouté".formatted(payload.get("plat").get("nom").asText());
                            break;
                        case "CommandeFinalisee":
                            message = "Commande finalisée";
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown eventType %s for functionalDomain %s"
                                    .formatted(auditEvent.eventType().type(), auditEvent.fromApplication().functionalDomain()));
                    }
                    break;
                case "Cuisine":
                    switch (auditEvent.eventType().type()) {
                        case "CommandeAProduire":
                            final JsonNode platsJsonNode = payload.get("plats");
                            Validate.validState(platsJsonNode.isArray());
                            final List<String> plats = new ArrayList<>();
                            platsJsonNode.elements().forEachRemaining(platJsonNode ->
                                    plats.add(platJsonNode.get("nom").asText()));
                            message = "Plats %s à préparer".formatted(String.join(",", plats));
                            break;
                        case "ProductionTerminee":
                            message = "Production terminée";
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown eventType %s for functionalDomain %s"
                                    .formatted(auditEvent.eventType().type(), auditEvent.fromApplication().functionalDomain()));
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown functionalDomain %s".formatted(auditEvent.fromApplication().functionalDomain()));
            }
            return AuditEventDTO.from(auditEvent, message);
        } catch (final DecryptionException e) {
            // can be the case
            return null;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
