package com.creperie.gestion.infrastructure.api;

import com.creperie.gestion.domain.AuditEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "AuditEvent", required = true, requiredProperties = {"functionalDomain", "creationDate", "eventType", "message"})
public record AuditEventDTO(String functionalDomain,
                            Instant creationDate,
                            String eventType,
                            String message) {

    public static AuditEventDTO from(final AuditEvent auditEvent, final String message) {
        return new AuditEventDTO(
                auditEvent.fromApplication().functionalDomain(),
                auditEvent.creationDate(),
                auditEvent.eventType().type(),
                message);
    }
}
